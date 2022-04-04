package de.innovationhub.prox.apigateway;


import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class OpenApiController {
  private static final String METADATA_CONFIG_KEY = "openApiPath";
  private final WebClient webClient;
  private final RouteLocator routeLocator;

  @Autowired
  public OpenApiController(RouteLocator routeLocator) {
    this.routeLocator = routeLocator;
    this.webClient = WebClient.builder().build();
  }

  @GetMapping(value = "v3/api-docs", params = "group")
  public Mono<ResponseEntity<String>> getOpenApiDefinitionFromService(
      @RequestParam("group") String group) {
    return this.getOpenApiUrl(group)
        .flatMap(
            it ->
                this.webClient
                    .get()
                    .uri(it)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(res -> res.bodyToMono(String.class))
                    .map(ResponseEntity::ok));
  }

  private Mono<URI> getOpenApiUrl(String serviceName) {
    return this.routeLocator
        .getRoutes()
        .filter(it -> it.getId().equals(serviceName))
        .filter(it -> it.getMetadata().get(METADATA_CONFIG_KEY) instanceof String)
        .map(it -> it.getUri().resolve((String) it.getMetadata().get(METADATA_CONFIG_KEY)))
        .next();
  }
}
