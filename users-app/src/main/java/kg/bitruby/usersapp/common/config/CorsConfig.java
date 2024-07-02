package kg.bitruby.usersapp.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  @Value("${bitruby.frontend.url}")
  private String frontendUrl;

  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.addAllowedOrigin(frontendUrl);
    corsConfig.addAllowedHeader("*");
    corsConfig.addAllowedMethod("GET");
    corsConfig.addAllowedMethod("POST");
    corsConfig.addAllowedMethod("OPTIONS");
    corsConfig.addAllowedMethod("HEAD");
    corsConfig.addAllowedMethod("DELETE");
    corsConfig.addAllowedMethod("PUT");
    corsConfig.addAllowedMethod("TRACE");
    corsConfig.addAllowedMethod("PATCH");
    corsConfig.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsFilter(source);
  }
}
