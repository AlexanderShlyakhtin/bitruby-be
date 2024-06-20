package kg.bitruby.authserver.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import kg.bitruby.authserver.config.customGrantTypes.emailPassword.EmailPasswordAuthenticationConverter;
import kg.bitruby.authserver.config.customGrantTypes.emailPassword.EmailPasswordAuthenticationProvider;
import kg.bitruby.authserver.config.customGrantTypes.emailPassword.EmailPasswordAuthenticationToken;
import kg.bitruby.authserver.config.customGrantTypes.phonePassword.PhonePasswordAuthenticationConverter;
import kg.bitruby.authserver.config.customGrantTypes.phonePassword.PhonePasswordAuthenticationProvider;
import kg.bitruby.authserver.config.customGrantTypes.phonePassword.PhonePasswordAuthenticationToken;
import kg.bitruby.authserver.config.model.CustomPasswordUser;
import kg.bitruby.authserver.entity.UserEntity;
import kg.bitruby.authserver.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.REFRESH_TOKEN;


@Configuration
public class SecurityConfig {

  @Autowired
  private UserInfoService userInfoService;


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
            .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "http://127.0.0.1:4200")));
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
    return a -> a.forEach(System.out::println);
  }

  private Consumer<List<AuthenticationProvider>> getProviders() {
    return a -> a.forEach(System.out::println);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("client")
        .clientSecret(passwordEncoder().encode("secret"))
        .scope(OidcScopes.PROFILE)
        .scope(OidcScopes.OPENID)
        .scope(OidcScopes.EMAIL)
        .scope("offline_access")
        .redirectUri("http://127.0.0.1:4200")
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(REFRESH_TOKEN)
        .authorizationGrantType(new AuthorizationGrantType("email_password"))
        .authorizationGrantType(new AuthorizationGrantType("phone_password"))
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
        UserEntity userInfo;
        if(context.getAuthorizationGrantType().getValue().equals("email_password")) {
          EmailPasswordAuthenticationToken authorization = (EmailPasswordAuthenticationToken) context.getAuthorizationGrant();
          userInfo = userInfoService.getUserInfoByEmail(authorization.getUsername());
        } else if(context.getAuthorizationGrantType().getValue().equals("phone_password")) {
          PhonePasswordAuthenticationToken authorization = (PhonePasswordAuthenticationToken) context.getAuthorizationGrant();
          userInfo = userInfoService.getUserInfoByPhone(authorization.getUsername());
        }
        else if(context.getAuthorizationGrantType().getValue().equals(REFRESH_TOKEN.getValue())) {
          OAuth2RefreshTokenAuthenticationToken authorization = (OAuth2RefreshTokenAuthenticationToken) context.getAuthorizationGrant();
          OAuth2Authorization byToken = authorizationService().findByToken(authorization.getRefreshToken(), OAuth2TokenType.REFRESH_TOKEN);
          assert byToken != null;
          if(byToken.getAuthorizationGrantType().getValue().equals("phone_password")) {
            OAuth2ClientAuthenticationToken token = (OAuth2ClientAuthenticationToken) byToken.getAttributes().get("java.security.Principal");
            CustomPasswordUser details = (CustomPasswordUser) token.getDetails();
            userInfo = userInfoService.getUserInfoByPhone(details.username());
          } else if(byToken.getAuthorizationGrantType().getValue().equals("email_password")) {
            OAuth2ClientAuthenticationToken token = (OAuth2ClientAuthenticationToken) byToken.getAttributes().get("java.security.Principal");
            CustomPasswordUser details = (CustomPasswordUser) token.getDetails();
            userInfo = userInfoService.getUserInfoByEmail(details.username());
          } else {
            throw new OAuth2AuthenticationException("Unsupported grant type");
          }
        }
        else {
          throw new OAuth2AuthenticationException("Unsupported grant type");
        }
        String level;
        if(!userInfo.isUserDataNonPending() && !userInfo.isRegistrationComplete() && userInfo.isAccountNonLocked()) {
          level = "0";
        } else if(!userInfo.isAccountNonLocked()) {
          level = "100";
        } else if(userInfo.isUserDataNonPending() && !userInfo.isRegistrationComplete()) {
          level = "1";
        } else if(userInfo.isRegistrationComplete()) {
          level = "2";
        } else {
          throw new OAuth2AuthenticationException("Illegal account status");
        }
        context.getClaims().claim("level", level);
      }
    };
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
