package de.innovationhub.prox.apigateway.userinfo.client;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.innovationhub.prox.apigateway.userinfo.client.dto.OrganizationDto;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClientImpl implements UserServiceClient {
  private static final String USER_SERVICE_ID = "user-service";
  private final RouteLocator routeLocator;
  private final AsyncCache<String, List<OrganizationDto>> cache;

  public UserServiceClientImpl(@Lazy RouteLocator routeLocator) {
    this.routeLocator = routeLocator;
    cache = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES) // Expire after token lifespan and a bit
        .maximumSize(1_000) // Should be more than enough
        .buildAsync();
  }

  @Override
  public Mono<List<OrganizationDto>> getOrganizationMembershipsWithToken(String token) {
    return Mono.fromFuture(() -> cache.get(token, (key, executor) -> getOrganizationMembershipsWithTokenFromRemote(key).toFuture()));
  }

  private Mono<List<OrganizationDto>> getOrganizationMembershipsWithTokenFromRemote(String token) {
    // Everytime we're going to perform a request we build a new client. This has been chosen
    // because it could be possible that URLs need to be resolved from a loadbalancer beforehand.
    return buildClient()
        .flatMap(client -> client.get()
            .uri("/user/organizations")
            .header("Authorization", "Bearer %s".formatted(token))
            .exchangeToMono(res ->
                res.bodyToMono(new ParameterizedTypeReference<>() {})));
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
