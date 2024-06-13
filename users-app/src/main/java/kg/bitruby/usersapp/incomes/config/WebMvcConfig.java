package kg.bitruby.usersapp.incomes.config;

import kg.bitruby.usersapp.common.config.CorsConfig;
import kg.bitruby.usersapp.incomes.interceptors.HeaderInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  @Bean
  public CorsFilter corsFilter() {
    return new CorsConfig().corsFilter();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new HeaderInterceptor())
        .addPathPatterns("/api/v1/**");
  }

}
