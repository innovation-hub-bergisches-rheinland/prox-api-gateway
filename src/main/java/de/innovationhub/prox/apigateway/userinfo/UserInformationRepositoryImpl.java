package de.innovationhub.prox.apigateway.userinfo;

import de.innovationhub.prox.apigateway.userinfo.client.UserServiceClient;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UserInformationRepositoryImpl implements UserInformationRepository {

  // TODO: Atm we do a synchronous call to the user-service. We should use async communication
  //       in the near future. Note that it might still be viable to reconcile the state using
  //       synchronous calls.
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
        .doOnError(err -> log.error("Could not get memberships", err))
        // If an error occurs, at least we can return the known information.
        .onErrorReturn(new UserInformation(token.getName(), Collections.emptySet()));
  }
}
