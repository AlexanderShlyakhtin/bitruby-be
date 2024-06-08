package com.bitruby.usersapp.incomes.config;

import com.bitruby.usersapp.common.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  @Bean
  public CorsFilter corsFilter() {
    return new CorsConfig().corsFilter();
  }

}
