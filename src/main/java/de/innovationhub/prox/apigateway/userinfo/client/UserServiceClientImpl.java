package de.innovationhub.prox.apigateway.userinfo.client;

import de.innovationhub.prox.apigateway.userinfo.client.dto.OrganizationDto;
import java.util.List;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClientImpl implements UserServiceClient {
  private static final String USER_SERVICE_ID = "user-service";
  private final RouteLocator routeLocator;

  public UserServiceClientImpl(RouteLocator routeLocator) {
    this.routeLocator = routeLocator;
  }

  @Override
  public Mono<List<OrganizationDto>> getOrganizationMembershipsWithToken(String token) {
    // Everytime we're going to perform a request we build a new client. This has been chosen
    // because it could be possible that URLs need to be resolved from a loadbalancer beforehand.
    return buildClient()
        .flatMap(client -> client.get()
          .uri("/user/organizations")
          .header("Authorization", "Bearer %s".formatted(token))
          .exchangeToMono(res ->
              res.bodyToMono(new ParameterizedTypeReference<>() {})
        ));
  }

  /**
   * Build the webclient from Route Locator
   * @return
   */
  private Mono<WebClient> buildClient() {
    return routeLocator.getRoutes()
        .filter(it -> it.getId().equals(USER_SERVICE_ID))
        .next()
        .map(route -> WebClient.builder().baseUrl(route.getUri().toString()).build());
  }
}
