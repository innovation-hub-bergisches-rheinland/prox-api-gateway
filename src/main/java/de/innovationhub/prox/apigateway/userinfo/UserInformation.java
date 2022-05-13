package de.innovationhub.prox.apigateway.userinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import java.util.UUID;

public record UserInformation(@JsonProperty("sub") String userId, @JsonProperty("orgs") Set<String> organizationIds) {
}
