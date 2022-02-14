package de.innovationhub.prox.apigateway;


import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.handler.FilteringWebHandler;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class OpenApiController {
  private final static String METADATA_CONFIG_KEY = "openApiPath";
  private final WebClient webClient;
  private final RouteLocator routeLocator;
  private final EurekaClient eurekaClient;

  @Autowired
  public OpenApiController(
      RouteLocator routeLocator,
      EurekaClient eurekaClient
  ) {
    this.routeLocator = routeLocator;
    this.eurekaClient = eurekaClient;
    this.webClient = WebClient.builder().build();
  }

  @GetMapping(value = "v3/api-docs", params = "group")
  public Mono<ResponseEntity<String>> getOpenApiDefinitionFromService(
      @RequestParam("group") String group, ServerWebExchange serverWebExchange) {
    return this.getOpenApiUrl(group)
        .flatMap(it -> this.webClient.get().uri(it)
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToMono(res -> res.bodyToMono(String.class))
        .map(ResponseEntity::ok));
  }

  private Mono<URI> getOpenApiUrl(String serviceName) {
    return this.routeLocator.getRoutes()
        .filter(it -> it.getId().equals(serviceName))
        .filter(it -> it.getMetadata().get(METADATA_CONFIG_KEY) instanceof String)
        .flatMap(it -> resolveUrl(it.getUri())
            .map(uri -> uri.resolve((String) it.getMetadata().get(METADATA_CONFIG_KEY))))
        .next();
  }

  // TODO: Instead of programmatically resolving loadbalancer urls we should apply the gateway
  //  filters to webclient requests. However, I don't have any clue how to do this and as we're
  //  probably replacing Eureka with k8s native load-balancing I didn't put much effort in it
  private Mono<URI> resolveUrl(URI serviceUri) {
    if(serviceUri.getScheme().equals("lb")) {
      return Mono.fromCallable(() -> eurekaClient.getNextServerFromEureka(serviceUri.getHost(), false))
          .map(instance -> {
            try {
              return new URI(instance.getHomePageUrl());
            } catch (URISyntaxException e) {
              e.printStackTrace();
              throw new RuntimeException(e);
            }
          });
    }
    return Mono.just(serviceUri);
  }
}
