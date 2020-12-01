package de.innovationhub.prox.apigateway;

import org.springdoc.core.SwaggerUiConfigParameters;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class SpringdocConfig {

  @Bean
  @Primary
  public SwaggerUiConfigParameters swaggerUiConfigParameters (SwaggerUiConfigProperties swaggerUiConfig){
    return new SwaggerUiConfigParameters(swaggerUiConfig);
  }

  @Bean
  @Primary
  public SwaggerUiConfigProperties swaggerUiConfigProperties() {
    return new SwaggerUiConfigProperties();
  }

}
