package kg.bitruby.authserver.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import kg.bitruby.authserver.api.model.GrantType;
import kg.bitruby.authserver.config.granttypes.email_password.EmailPasswordAuthenticationConverter;
import kg.bitruby.authserver.config.granttypes.email_password.EmailPasswordAuthenticationProvider;
import kg.bitruby.authserver.config.granttypes.email_password.EmailPasswordAuthenticationToken;
import kg.bitruby.authserver.config.granttypes.phone_password.PhonePasswordAuthenticationConverter;
import kg.bitruby.authserver.config.granttypes.phone_password.PhonePasswordAuthenticationProvider;
import kg.bitruby.authserver.config.granttypes.phone_password.PhonePasswordAuthenticationToken;
import kg.bitruby.authserver.config.model.CustomPasswordUser;
import kg.bitruby.authserver.entity.UserEntity;
import kg.bitruby.authserver.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static kg.bitruby.authserver.api.model.GrantType.EMAIL_PASSWORD;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.REFRESH_TOKEN;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

  @Value("${bitruby.auth.client-id}")
  private String clientId;

  @Value("${bitruby.auth.client-secret}")
  private String clientSecret;

  private final UserInfoService userInfoService;

  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
        .tokenEndpoint(tokenEndpoint -> tokenEndpoint
            .accessTokenRequestConverter(new EmailPasswordAuthenticationConverter())
            .authenticationProvider(new EmailPasswordAuthenticationProvider(authorizationService(),
                passwordEncoder(), tokenGenerator()))
            .accessTokenRequestConverters(getConverters())
            .authenticationProviders(getProviders())
        )
        .tokenEndpoint(tokenEndpoint -> tokenEndpoint
            .accessTokenRequestConverter(new PhonePasswordAuthenticationConverter())
            .authenticationProvider(new PhonePasswordAuthenticationProvider(authorizationService(),passwordEncoder(), tokenGenerator() ))
            .accessTokenRequestConverters(getConverters())
            .authenticationProviders(getProviders())
        )
        .oidc(withDefaults());

    http.headers(httpSecurityHeadersConfigurer ->
        httpSecurityHeadersConfigurer
            .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "http://185.17.141.84:4200")));
    http.cors(cors -> cors.configure(http));
    http.exceptionHandling(e -> e
        .defaultAuthenticationEntryPointFor(
            new LoginUrlAuthenticationEntryPoint("/login"),
            new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
        ));

    return http.build();
  }

  @Bean
  public OAuth2AuthorizationService authorizationService() {
    return new InMemoryOAuth2AuthorizationService();
  }

  @Bean
  public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService() {
    return new InMemoryOAuth2AuthorizationConsentService();
  }

  private Consumer<List<AuthenticationConverter>> getConverters() {
    return a -> a.forEach(authenticationConverter -> log.info("Converters: {}",authenticationConverter.toString()));
  }

  private Consumer<List<AuthenticationProvider>> getProviders() {
    return a -> a.forEach(authenticationConverter -> log.info("Provider: {}", authenticationConverter.toString()));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId(clientId)
        .clientSecret(passwordEncoder().encode(clientSecret))
        .scope(OidcScopes.PROFILE)
        .scope(OidcScopes.OPENID)
        .scope(OidcScopes.EMAIL)
        .scope("offline_access")
        .redirectUri("http://185.17.141.84:4200")
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(REFRESH_TOKEN)
        .authorizationGrantType(new AuthorizationGrantType(EMAIL_PASSWORD.getValue()))
        .authorizationGrantType(new AuthorizationGrantType(GrantType.PHONE_PASSWORD.getValue()))
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)
        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
        .tokenSettings(tokenSettings())
        .clientSettings(ClientSettings.builder().requireProofKey(true).build())
        .build();

    return new InMemoryRegisteredClientRepository(registeredClient);
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().build();
  }

  @Bean
  public TokenSettings tokenSettings() {
    return TokenSettings.builder()
        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
        .refreshTokenTimeToLive(Duration.ofMinutes(1800))
        .accessTokenTimeToLive(Duration.ofMinutes(600))
        .build();
  }

  @Bean
  public ClientSettings clientSettings() {
    return ClientSettings.builder()
        .requireAuthorizationConsent(false)
        .requireProofKey(true)
        .build();
  }

  @Bean
  public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator() {
    NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource());
    JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
    jwtGenerator.setJwtCustomizer(tokenCustomizer(userInfoService));
    OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
    OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
    return new DelegatingOAuth2TokenGenerator(
        jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
  }

  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UserInfoService userInfoService) {
    return context -> {
      if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
        UserEntity userInfo = getUserInfo(context, userInfoService);
        String level = determineUserLevel(userInfo);
        context.getClaims().claim("level", level);
        context.getClaims().claim("sub", userInfo.getEmail());
      }
    };
  }

  private UserEntity getUserInfo(JwtEncodingContext context, UserInfoService userInfoService) {
    String grantType = context.getAuthorizationGrantType().getValue();
    UserEntity userInfo;

    switch (GrantType.fromValue(grantType)) {
      case EMAIL_PASSWORD:
        EmailPasswordAuthenticationToken emailAuth = (EmailPasswordAuthenticationToken) context.getAuthorizationGrant();
        userInfo = userInfoService.getUserInfoByEmail(emailAuth.getUsername());
        break;

      case PHONE_PASSWORD:
        PhonePasswordAuthenticationToken phoneAuth = (PhonePasswordAuthenticationToken) context.getAuthorizationGrant();
        userInfo = userInfoService.getUserInfoByPhone(phoneAuth.getUsername());
        break;

      case REFRESH_TOKEN:
        OAuth2RefreshTokenAuthenticationToken refreshAuth = (OAuth2RefreshTokenAuthenticationToken) context.getAuthorizationGrant();
        OAuth2Authorization authorization = authorizationService().findByToken(refreshAuth.getRefreshToken(), OAuth2TokenType.REFRESH_TOKEN);
        if (authorization == null) {
          throw new OAuth2AuthenticationException("Authorization not found");
        }
        userInfo = getUserInfoFromRefreshToken(authorization, userInfoService);
        break;

      default:
        throw new OAuth2AuthenticationException("Unsupported grant type");
    }
    return userInfo;
  }

  private UserEntity getUserInfoFromRefreshToken(OAuth2Authorization authorization, UserInfoService userInfoService) {
    OAuth2ClientAuthenticationToken token = (OAuth2ClientAuthenticationToken) authorization.getAttributes().get("java.security.Principal");
    CustomPasswordUser details = (CustomPasswordUser) token.getDetails();
    String grantType = authorization.getAuthorizationGrantType().getValue();

    switch (GrantType.fromValue(grantType)) {
      case PHONE_PASSWORD:
        return userInfoService.getUserInfoByPhone(details.username());
      case EMAIL_PASSWORD:
        return userInfoService.getUserInfoByEmail(details.username());
      default:
        throw new OAuth2AuthenticationException("Unsupported grant type");
    }
  }

  private String determineUserLevel(UserEntity userInfo) {
    if(!userInfo.isEnabled()) {
      throw new OAuth2AuthenticationException("Account not enable");
    } else if (!userInfo.isAccountNonLocked()) {
      return "100";
    } else if (!userInfo.isVerified() && !userInfo.isBybitAccountCreated() && !userInfo.isRegistrationComplete() ) {
      return "0";
    }  else if (userInfo.isVerified() && !userInfo.isRegistrationComplete()) {
      return "1";
    } else if (userInfo.isBybitAccountCreated() && !userInfo.isRegistrationComplete()) {
      return "2";
    } else if (userInfo.isRegistrationComplete()) {
      return "3";
    } else {
      throw new OAuth2AuthenticationException("Illegal account status");
    }
  }

  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    RSAKey rsaKey = generateRsa();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
  }

  private static RSAKey generateRsa() {
    KeyPair keyPair = generateRsaKey();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
  }

  private static KeyPair generateRsaKey() {
    KeyPair keyPair;
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      keyPair = keyPairGenerator.generateKeyPair();
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
    return keyPair;
  }
}
