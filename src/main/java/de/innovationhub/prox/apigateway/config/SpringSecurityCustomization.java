package de.innovationhub.prox.apigateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/*
 * As we're using Spring Boot OAuth 2.0 Resource Server, spring boot comes with defaults that
 * might not be applicable for the gateway implementation. This configuration class is meant to
 * configure those defaults.
 */
@Configuration
public class SpringSecurityCustomization {

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.authorizeExchange(
        // At the moment we don't want to do any authorization in the gateway.
        authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll()
        )
        // But we need the JWT configuration as we're going to introspect it
        .oauth2ResourceServer(
            oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(Customizer.withDefaults()));
    return http.build();
  }
}
