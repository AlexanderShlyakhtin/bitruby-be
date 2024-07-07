package kg.bitruby.authservice.common.config.granttypes.phone_password;

import kg.bitruby.authservice.api.model.GrantType;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.*;

public class PhonePasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

  private static final long serialVersionUID = 1L;
  private final String username;
  private final String password;
  private final Set<String> scopes;
  private final String otp;
  private final UUID loginId;

  public PhonePasswordAuthenticationToken(Authentication clientPrincipal,
      @Nullable Set<String> scopes, @Nullable Map<String, Object> additionalParameters, String otp,
      String loginId) {
    super(new AuthorizationGrantType(GrantType.PHONE_PASSWORD.getValue()), clientPrincipal, additionalParameters);
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
