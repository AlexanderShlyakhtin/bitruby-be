package kg.bitruby.usersapp.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.addAllowedOrigin("http://185.17.141.84:4200");
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
