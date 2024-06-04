package com.bitruby.authserver.config.customGrantTypes.phonePassword;

import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Map;

public class PhonePasswordOAuth2ClientAuthenticationToken extends OAuth2ClientAuthenticationToken {

  private static final long serialVersionUID = 1L;

  public PhonePasswordOAuth2ClientAuthenticationToken(String clientId,
      ClientAuthenticationMethod clientAuthenticationMethod, Object credentials,
      Map<String, Object> additionalParameters) {
    super(clientId, clientAuthenticationMethod, credentials, additionalParameters);
  }

  public PhonePasswordOAuth2ClientAuthenticationToken(RegisteredClient registeredClient,
      ClientAuthenticationMethod clientAuthenticationMethod, Object credentials) {
    super(registeredClient, clientAuthenticationMethod, credentials);
  }
}
