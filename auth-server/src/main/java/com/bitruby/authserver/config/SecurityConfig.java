package com.bitruby.authserver.config;

import com.bitruby.authserver.config.customGrantTypes.emailPassword.EmailPasswordAuthenticationConverter;
import com.bitruby.authserver.config.customGrantTypes.emailPassword.EmailPasswordAuthenticationProvider;
import com.bitruby.authserver.config.customGrantTypes.phonePassword.PhonePasswordAuthenticationConverter;
import com.bitruby.authserver.config.customGrantTypes.phonePassword.PhonePasswordAuthenticationProvider;
import com.bitruby.authserver.config.model.CustomPasswordUser;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.security.Security.getProviders;
import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
public class SecurityConfig {

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
    http.cors(cors -> cors.configurationSource(corsFilter()));
    http.headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "http://127.0.0.1:4200")));
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
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
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

//  @Bean
//  public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UserInfoService userInfoService) {
//    return context -> {
//      if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
//        String name = context.getPrincipal().getName();
//        UserEntity userInfo = userInfoService.getUserInfo(name);
//        context.getClaims().claim("role", userInfo.getRole());
//      }
//      if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
//        String name = context.getPrincipal().getName();
//        UserEntity userInfo = userInfoService.getUserInfo(name);
//      }
//    };
//  }

  public CorsConfigurationSource corsFilter() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(List.of("http://127.0.0.1:4200"));
    configuration.setAllowedHeaders(List.of("*", "authorization"));
    configuration.setAllowedMethods(
        Arrays.asList("GET","POST", "OPTIONS", "HEAD", "DELETE", "PUT", "TRACE"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/.well-known/openid-configuration", configuration);
    source.registerCorsConfiguration("/userinfo", configuration);
    source.registerCorsConfiguration("/login", configuration);
    source.registerCorsConfiguration("/oauth2/jwks", configuration);
    source.registerCorsConfiguration("/oauth2/authorize", configuration);
    source.registerCorsConfiguration("/oauth2/token", configuration);
    source.registerCorsConfiguration("/oauth2/introspect", configuration);
    source.registerCorsConfiguration("/oauth2/revoke", configuration);

    return source;
  }

  @Bean
  public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator() {
    NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource());
    JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
//    jwtGenerator.setJwtCustomizer(tokenCustomizer());
    OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
    OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
    return new DelegatingOAuth2TokenGenerator(
        jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
  }

//  @Bean
//  public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
//    return context -> {
//    };
//  }


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
