package de.innovationhub.prox.apigateway.userinfo;

import java.util.UUID;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

public interface UserInformationRepository {
  Mono<UserInformation> findWithToken(JwtAuthenticationToken token);
}
