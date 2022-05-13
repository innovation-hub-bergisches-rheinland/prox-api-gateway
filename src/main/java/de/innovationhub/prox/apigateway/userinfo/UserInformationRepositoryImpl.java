package de.innovationhub.prox.apigateway.userinfo;

import de.innovationhub.prox.apigateway.userinfo.client.UserServiceClient;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UserInformationRepositoryImpl implements UserInformationRepository {

  private final UserServiceClient userServiceClient;

  public UserInformationRepositoryImpl(UserServiceClient userServiceClient) {
    this.userServiceClient = userServiceClient;
  }

  @Override
  public Mono<UserInformation> findWithToken(JwtAuthenticationToken token) {
    return this.userServiceClient.getOrganizationMembershipsWithToken(
            token.getToken().getTokenValue())
        .map(res -> new UserInformation(token.getName(),
            res.stream().map(dto -> dto.id().toString()).collect(Collectors.toUnmodifiableSet())))
        .doOnError(err -> log.error("Could not get memberships", err));
  }
}
