package kg.bitruby.usersapp.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/v1/public/**").permitAll()
            .requestMatchers("/error").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .authenticationManagerResolver(new JwtIssuerAuthenticationManagerResolver("http://auth-server:9000/auth/api/v1")))
        .cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable);
    http.headers(httpSecurityHeadersConfigurer ->
        httpSecurityHeadersConfigurer
            .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "http://127.0.0.1:4200")));
    return http.build();
  }

}
