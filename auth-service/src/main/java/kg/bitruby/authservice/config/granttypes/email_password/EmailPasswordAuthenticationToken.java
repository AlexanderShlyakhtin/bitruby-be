package kg.bitruby.authservice.config.granttypes.email_password;

import kg.bitruby.authservice.api.model.GrantType;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.*;

public class EmailPasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

  private static final long serialVersionUID = 1L;
  private final String username;
  private final String password;
  private final Set<String> scopes;
  private final String otp;
  private final UUID loginId;

  public EmailPasswordAuthenticationToken(Authentication clientPrincipal,
      @Nullable Set<String> scopes, @Nullable Map<String, Object> additionalParameters, String otp,
      String loginId) {
    super(new AuthorizationGrantType(GrantType.EMAIL_PASSWORD.getValue()), clientPrincipal, additionalParameters);
    this.username = (String) additionalParameters.get("username");
    this.password = (String) additionalParameters.get("password");
    this.otp = (String) additionalParameters.get("otp");
    this.scopes = Collections.unmodifiableSet(
        scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
    this.loginId = UUID.fromString(loginId);
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }

  public Set<String> getScopes() {
    return this.scopes;
  }

  public String getOtp() {
    return otp;
  }

  public UUID getLoginId() {
    return loginId;
  }
}
