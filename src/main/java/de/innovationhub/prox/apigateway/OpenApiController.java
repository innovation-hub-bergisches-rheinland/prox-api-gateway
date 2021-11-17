package de.innovationhub.prox.apigateway;


import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

  private final EurekaClient eurekaClient;
  private final WebClient webClient;

  @Autowired
  public OpenApiController(@Qualifier("eurekaClient") EurekaClient eurekaClient) {
    this.eurekaClient = eurekaClient;
    this.webClient = WebClient.builder().build();
  }

  @GetMapping(value = "v3/api-docs", params = "group")
  public Mono<ResponseEntity<String>> getOpenApiDefinitionFromService(
      @RequestParam("group") String group) {
    String url = getServiceUrl(group);

    if (url == null) {
      return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    return this.webClient
        .get()
        .uri(url + "v3/api-docs?group={group}", group)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .flatMap(res -> res.bodyToMono(String.class))
        .map(ResponseEntity::ok);
  }

  private String getServiceUrl(String serviceName) {
    try {
      return this.eurekaClient.getNextServerFromEureka(serviceName, false).getHomePageUrl();
    } catch (Exception e) {
      log.warn("Could not retrieve service '" + serviceName + "' url");
      return null;
    }
  }
}
