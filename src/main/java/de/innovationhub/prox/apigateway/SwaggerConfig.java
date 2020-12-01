package de.innovationhub.prox.apigateway;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;


@Configuration
public class SwaggerConfig {

  private static final String[] SERVICES_TO_INCLUDE = {"module-service", "project-service", "tag-service"};

  @Primary
  @Bean
  public SwaggerResourcesProvider swaggerResourcesProvider(InMemorySwaggerResourcesProvider resourcesProvider) {
    return () -> {
      List<SwaggerResource> resources = resourcesProvider.get();
      resources.clear();
      resources.addAll(Arrays.stream(SERVICES_TO_INCLUDE).map(service -> {
        SwaggerResource resource = new SwaggerResource();
        resource.setName(service);
        resource.setLocation("/v3/api-docs?group=" + service);
        return resource;
      }).collect(Collectors.toList()));
      return resources;
    };
  }

}
