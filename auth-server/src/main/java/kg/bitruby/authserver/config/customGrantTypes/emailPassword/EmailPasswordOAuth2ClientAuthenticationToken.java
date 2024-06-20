package kg.bitruby.authserver.config.customGrantTypes.emailPassword;

import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Map;

public class EmailPasswordOAuth2ClientAuthenticationToken extends OAuth2ClientAuthenticationToken {

  private static final long serialVersionUID = 1L;

  public EmailPasswordOAuth2ClientAuthenticationToken(String clientId,
      ClientAuthenticationMethod clientAuthenticationMethod, Object credentials,
      Map<String, Object> additionalParameters) {
    super(clientId, clientAuthenticationMethod, credentials, additionalParameters);
  }

  public EmailPasswordOAuth2ClientAuthenticationToken(RegisteredClient registeredClient,
      ClientAuthenticationMethod clientAuthenticationMethod, Object credentials) {
    super(registeredClient, clientAuthenticationMethod, credentials);
  }
}
