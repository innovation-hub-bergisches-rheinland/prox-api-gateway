package de.innovationhub.prox.apigateway;

import com.netflix.discovery.EurekaClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.AbstractSwaggerUiConfigProperties.SwaggerUrl;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OpenApiImportTask {
  private final EurekaClient eurekaClient;
  private final SwaggerUiConfigParameters swaggerUiConfigParameters;
  private static final Map<String, String> SERVICE_NAME_OAI_SUFFIX = new HashMap<>();

  @Autowired
  public OpenApiImportTask(@Qualifier("eurekaClient") EurekaClient eurekaClient, SwaggerUiConfigParameters swaggerUiConfigParameters) {
    this.eurekaClient = eurekaClient;
    this.swaggerUiConfigParameters = swaggerUiConfigParameters;

    //NOTE: Should be replaced by something more precise
    SERVICE_NAME_OAI_SUFFIX.put("module-service", "v3/api-docs?group=prox-module-service");
    SERVICE_NAME_OAI_SUFFIX.put("project-service", "v3/api-docs?group=prox-project-service");
    SERVICE_NAME_OAI_SUFFIX.put("tag-service", "v3/api-docs?group=prox-tag-service");
  }

  /**
   * Returns Service URLs associated to its service name
   * @param serviceNames List of service names
   * @return Map (Key: service name, Value: Service URL)
   */
  private Map<String, String> getServiceUrls(List<String> serviceNames) {
    return serviceNames.stream()
        .map(name -> new String[]{ name, getServiceUrl(name)})
        .filter(s -> Objects.nonNull(s[1]))
        .collect(Collectors.toMap(s -> s[0], s -> s[1], (prev, next) -> next, HashMap::new));
  }

  /**
   * Returns the service URL registered in eureka
   * @param serviceName Service Name
   * @return URL
   */
  private String getServiceUrl(String serviceName) {
    try {
      return this.eurekaClient.getNextServerFromEureka(serviceName, false).getHomePageUrl();
    } catch (Exception e) {
      log.warn("Could not retrieve service '" + serviceName + "' url");
      return null;
    }
  }

  /**
   * Sheduled Task which will be executed every 5 Minutes
   * Adds the expected OpenAPI spec URL to SwaggerUI
   * TODO Check if URL is reachable and adjust interval
   */
  @Scheduled(fixedRate = 300_000)
  public void importOpenApiServices() {
    log.info("Importing OpenAPI Specs from services...");
    Map<String, String> services = getServiceUrls(new ArrayList<>(SERVICE_NAME_OAI_SUFFIX.keySet()));
    swaggerUiConfigParameters.getUrls().clear();
    services.forEach((s, s2) -> swaggerUiConfigParameters.getUrls().add(new SwaggerUrl(s, s2 + SERVICE_NAME_OAI_SUFFIX.get(s))));
    log.info("Importing OpenAPI Spec complete");
  }
}
