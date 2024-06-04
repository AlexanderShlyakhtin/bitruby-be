package com.bitruby.authserver.config;

import com.bitruby.authserver.service.CustomAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class DefaultSecurityConfig {

  private final CustomAuthenticationProvider customAuthenticationProvider;

  @Autowired
  public void bindAuthenticationProvider(AuthenticationManagerBuilder authenticationManagerBuilder) {
    authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
  }

  @Bean
  @Order(2)
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/error").permitAll()
            .anyRequest().authenticated())
        .formLogin(formLogin -> formLogin
            .loginPage("/login")
            .permitAll());
    return http.build();
  }

  @Bean
  WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.debug(false)
        .ignoring()
        .requestMatchers("/webjars/**", "/images/**", "/css/**", "/assets/**", "/favicon.ico");
  }

}
