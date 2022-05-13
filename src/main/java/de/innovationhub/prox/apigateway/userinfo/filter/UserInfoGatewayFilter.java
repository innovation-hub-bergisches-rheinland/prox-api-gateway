package de.innovationhub.prox.apigateway.userinfo.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.apigateway.userinfo.UserInformationRepository;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filter that resolves user information and appends it to the request headers.
 */
@Component
public class UserInfoGatewayFilter implements GlobalFilter {
  private static final String USER_INFO_HEADER = "X-UserInfo";

  private final UserInformationRepository userInformationRepository;
  private final ObjectMapper objectMapper;

  public UserInfoGatewayFilter(UserInformationRepository userInformationRepository,
      ObjectMapper objectMapper) {
    this.userInformationRepository = userInformationRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    return exchange.getPrincipal()
        .switchIfEmpty(Mono.error(new RuntimeException("Not authenticated")))
        .cast(JwtAuthenticationToken.class)
        .flatMap(userInformationRepository::findWithToken)
        .doOnNext(userInformation -> {
          try {
            exchange.getRequest().getHeaders().add(USER_INFO_HEADER, objectMapper.writeValueAsString(userInformation));
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        })
        .flatMap(it -> chain.filter(exchange))
        // We want to continue the filter chain if the user information cannot be resolved. This
        // could occur for example if the user is not authenticated and anonymous requests are
        // performed which is okay from the perspective of the filters' responsibility.
        .onErrorResume(it -> chain.filter(exchange));
  }
}
