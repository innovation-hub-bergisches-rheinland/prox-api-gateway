package de.innovationhub.prox.apigateway.userinfo.client;

import de.innovationhub.prox.apigateway.userinfo.client.dto.OrganizationDto;
import java.util.List;
import reactor.core.publisher.Mono;

public interface UserServiceClient {
  Mono<List<OrganizationDto>> getOrganizationMembershipsWithToken(String token);
}
