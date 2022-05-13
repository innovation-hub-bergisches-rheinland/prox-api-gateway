package de.innovationhub.prox.apigateway.userinfo.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.apigateway.userinfo.UserInformationRepository;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filter that resolves user information and appends it to the request headers.
 */
@Component
public class AddUserInfoHeaderGatewayFilterFactory extends
    AbstractGatewayFilterFactory<AddUserInfoHeaderGatewayFilterFactory.Config> {

  private final UserInformationRepository userInformationRepository;
  private final ObjectMapper objectMapper;

  public AddUserInfoHeaderGatewayFilterFactory(UserInformationRepository userInformationRepository,
      ObjectMapper objectMapper) {
    super(Config.class);
    this.userInformationRepository = userInformationRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> exchange.getPrincipal()
        .switchIfEmpty(Mono.error(new RuntimeException("Not authenticated")))
        .cast(JwtAuthenticationToken.class)
        .flatMap(userInformationRepository::findWithToken)
        .map(userInformation -> {
          try {
            return exchange.getRequest().mutate()
                .header(config.getHeader(), objectMapper.writeValueAsString(userInformation))
                .build();
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        })
        // We want to continue the filter chain if the user information cannot be resolved. This
        // could occur for example if the user is not authenticated and anonymous requests are
        // performed which is okay from the perspective of the filters' responsibility.
        .onErrorReturn(exchange.getRequest())
        .flatMap(request -> chain.filter(exchange.mutate().request(request).build()));
  }

  public static class Config {

    private String header = "X-UserInfo";

    public String getHeader() {
      return header;
    }

    public void setHeader(String header) {
      this.header = header;
    }
  }
}
