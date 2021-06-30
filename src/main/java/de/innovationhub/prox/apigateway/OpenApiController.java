package de.innovationhub.prox.apigateway;

import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class OpenApiController {

  private final EurekaClient eurekaClient;
  private final RestTemplateBuilder restTemplateBuilder;

  @Autowired
  public OpenApiController(@Qualifier("eurekaClient") EurekaClient eurekaClient) {
    this.eurekaClient = eurekaClient;
    this.restTemplateBuilder = new RestTemplateBuilder();
  }

  @Bean
  public RestTemplate restTemplate() {
    return this.restTemplateBuilder.build();
  }

  @GetMapping(value = "v3/api-docs", params = "group")
  public ResponseEntity<String> getOpenApiDefinitionFromService(@RequestParam("group") String group) {
    String url = getServiceUrl(group);

    if(url == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    RestTemplate restTemplate = restTemplateBuilder.build();

    String openAPI = restTemplate.getForObject(url + "v3/api-docs?group={group}", String.class,  group);
    if(openAPI == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return ResponseEntity.ok(openAPI);
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
