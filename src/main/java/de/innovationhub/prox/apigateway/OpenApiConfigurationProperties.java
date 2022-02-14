package de.innovationhub.prox.apigateway;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the inclusion of OpenAPI documents in the Gateway.
 *
 * TODO: We should "hook" ourselves directly into the `spring.cloud.gateway.routes` configuration
 *  or find another way to make the configuration more explicit as we only have a weak reference
 *  between service ids and OpenAPI definition
 */
@Configuration
@ConfigurationProperties(prefix = OpenApiConfigurationProperties.PREFIX)
public class OpenApiConfigurationProperties {
  public static final String PREFIX = "openapi";

  /**
   * OpenAPI Definitions
   * Note, that the key must be a corresponding service Id in spring.cloud.gateway.routes
   */
  private Map<String, OpenApiDefinition> definitions;

  public Map<String, OpenApiDefinition> getDefinitions() {
    return definitions;
  }

  public void setDefinitions(
      Map<String, OpenApiDefinition> definitions) {
    this.definitions = definitions;
  }

  public static class OpenApiDefinition {

    /**
     * Path Segment where the OpenAPI document is located
     */
    private String path;

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }
  }
}
