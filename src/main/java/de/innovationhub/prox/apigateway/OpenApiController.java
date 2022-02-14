package de.innovationhub.prox.apigateway;


import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class OpenApiController {
  private final WebClient webClient;
  private final RouteDefinitionLocator routeDefinitionLocator;
  private final OpenApiConfigurationProperties openApiConfigurationProperties;

  @Autowired
  public OpenApiController(
      RouteDefinitionLocator routeDefinitionLocator,
      OpenApiConfigurationProperties openApiConfigurationProperties) {
    this.routeDefinitionLocator = routeDefinitionLocator;
    this.openApiConfigurationProperties = openApiConfigurationProperties;
    this.webClient = WebClient.builder().build();
  }

  @GetMapping(value = "v3/api-docs", params = "group")
  public Mono<ResponseEntity<String>> getOpenApiDefinitionFromService(
      @RequestParam("group") String group) {
    return this.getOpenApiUrl(group)
        .flatMap(it -> this.webClient.get().uri(it)
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToMono(res -> res.bodyToMono(String.class))
        .map(ResponseEntity::ok));
  }

  private Mono<URI> getOpenApiUrl(String serviceName) {
    var definition = this.openApiConfigurationProperties.getDefinitions().get(serviceName);
    if(definition == null) {
      log.error("No Definition found for service " + serviceName);
      return Mono.error(new RuntimeException("No Definition found for service " + serviceName));
    }
    if(definition.getPath() == null) {
      log.error("No OpenAPI Path defined for service " + serviceName);
      return Mono.error(new RuntimeException("No OpenAPI Path defined for service " + serviceName));
    }
    return this.locateService(serviceName)
        .map(s -> s.resolve(definition.getPath()));
  }

  private Mono<URI> locateService(String serviceName) {
    return this.routeDefinitionLocator.getRouteDefinitions()
        .filter(it -> it.getId().equals(serviceName))
        .map(RouteDefinition::getUri)
        .next();
  }
}
