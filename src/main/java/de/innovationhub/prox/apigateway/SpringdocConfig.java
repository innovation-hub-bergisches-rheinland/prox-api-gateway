package de.innovationhub.prox.apigateway;

import com.netflix.discovery.EurekaClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springdoc.core.AbstractSwaggerUiConfigProperties.SwaggerUrl;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocConfigProperties.GroupConfig;
import org.springdoc.core.SpringDocConfiguration;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;


@Configuration
public class SpringdocConfig {

  private final EurekaClient eurekaClient;

  @Autowired
  public SpringdocConfig(@Qualifier("eurekaClient") EurekaClient eurekaClient) {
    this.eurekaClient = eurekaClient;
  }

  private String getServiceUrl(String serviceName, boolean secure, String defaultUrl) {
    try {
      return this.eurekaClient.getNextServerFromEureka(serviceName, secure).getHomePageUrl();
    } catch (Exception e) {
      return defaultUrl;
    }
  }

  @Bean
  @Primary
  public SwaggerUiConfigParameters swaggerUiConfigParameters (SwaggerUiConfigProperties swaggerUiConfig){
    SwaggerUiConfigParameters swaggerUiConfigParameters = new SwaggerUiConfigParameters(swaggerUiConfig);

    swaggerUiConfigParameters.getUrls().addAll(Arrays.asList(
        new SwaggerUrl("prox-module-service", getServiceUrl("module-service", false, "") + "v3/api-docs?group=prox-module-service"),
        new SwaggerUrl("prox-project-service", getServiceUrl("project-service", false, "") + "v3/api-docs?group=prox-project-service"),
        new SwaggerUrl("prox-tag-service", getServiceUrl("tag-service", false, "") + "v3/api-docs?group=prox-tag-service")));
    return swaggerUiConfigParameters;
  }

  @Bean
  @Primary
  public SwaggerUiConfigProperties swaggerUiConfigProperties() {
    return new SwaggerUiConfigProperties();
  }

}
