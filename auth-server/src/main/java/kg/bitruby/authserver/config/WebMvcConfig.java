package kg.bitruby.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Bean
  public CorsFilter corsFilter() {
    return corsFilterBuilt();
  }

  public CorsFilter corsFilterBuilt() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.addAllowedOrigin("http://127.0.0.1:4200");
    corsConfig.addAllowedHeader("*");
    corsConfig.addAllowedHeader("authorization");
    corsConfig.addAllowedMethod("GET");
    corsConfig.addAllowedMethod("POST");
    corsConfig.addAllowedMethod("OPTIONS");
    corsConfig.addAllowedMethod("HEAD");
    corsConfig.addAllowedMethod("DELETE");
    corsConfig.addAllowedMethod("PUT");
    corsConfig.addAllowedMethod("TRACE");
    corsConfig.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/.well-known/openid-configuration", corsConfig);
    source.registerCorsConfiguration("/userinfo", corsConfig);
    source.registerCorsConfiguration("/login", corsConfig);

    source.registerCorsConfiguration("/oauth2/jwks", corsConfig);
    source.registerCorsConfiguration("/oauth2/authorize", corsConfig);
    source.registerCorsConfiguration("/oauth2/token", corsConfig);
    source.registerCorsConfiguration("/oauth2/introspect", corsConfig);
    source.registerCorsConfiguration("/oauth2/revoke", corsConfig);

    return new CorsFilter(source);
  }

}



