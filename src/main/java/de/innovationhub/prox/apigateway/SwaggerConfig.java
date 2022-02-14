package de.innovationhub.prox.apigateway;


import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

@Configuration
public class SwaggerConfig {

  private final Set<String> SERVICES_TO_INCLUDE;

  public SwaggerConfig(OpenApiConfigurationProperties openApiConfigurationProperties) {
    this.SERVICES_TO_INCLUDE = openApiConfigurationProperties.getDefinitions()
        .keySet();
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
