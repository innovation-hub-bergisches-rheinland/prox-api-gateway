package de.innovationhub.prox.apigateway;


import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.boot.starter.autoconfigure.SpringfoxConfigurationProperties.OpenApiConfigurationProperties;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

@Configuration
public class SwaggerConfig {
  private final static String METADATA_CONFIG_KEY = "openApiPath";
  private final List<String> SERVICES_TO_INCLUDE;

  public SwaggerConfig(RouteLocator routeLocator) {
    try {
      this.SERVICES_TO_INCLUDE = routeLocator.getRoutes()
          .filter(it -> it.getMetadata().get(METADATA_CONFIG_KEY) instanceof String)
          .map(it -> it.getId())
          .collectList().toFuture().get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Primary
  @Bean
  public SwaggerResourcesProvider swaggerResourcesProvider(
      InMemorySwaggerResourcesProvider resourcesProvider) {
    return () -> {
      List<SwaggerResource> resources = resourcesProvider.get();
      resources.clear();
      resources.addAll(
          SERVICES_TO_INCLUDE.stream()
              .map(
                  service -> {
                    SwaggerResource resource = new SwaggerResource();
                    resource.setName(service);
                    resource.setLocation("/v3/api-docs?group=" + service);
                    return resource;
                  })
              .collect(Collectors.toList()));
      return resources;
    };
  }
}
