package de.innovationhub.prox.apigateway.userinfo.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record OrganizationDto(@JsonProperty("id") UUID id) {}
