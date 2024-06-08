package com.bitruby.usersapp.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  public CorsFilter corsFilter() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.addAllowedOrigin("http://127.0.0.1:4200");
    corsConfig.addAllowedOrigin("http://127.0.0.1:4201");
    corsConfig.addAllowedOrigin("https://editor.swagger.io");
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
