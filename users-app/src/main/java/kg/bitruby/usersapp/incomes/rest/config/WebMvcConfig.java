package kg.bitruby.usersapp.incomes.rest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.bitruby.usersapp.core.verification.HmacVerifier;
import kg.bitruby.usersapp.core.verification.VerificationService;
import kg.bitruby.usersapp.incomes.rest.interceptors.CheckHMacSignatureInterceptor;
import kg.bitruby.usersapp.incomes.rest.interceptors.HeaderInterceptor;
import kg.bitruby.usersapp.incomes.rest.interceptors.TokenInterceptor;
import kg.bitruby.usersapp.outcomes.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final UserRepository userRepository;
  private final HmacVerifier hmacVerifier;
  private final VerificationService verificationService;
  private final ObjectMapper objectMapper;


  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new CheckHMacSignatureInterceptor(hmacVerifier, verificationService, objectMapper))
        .addPathPatterns("/api/v1/public/verification/**");
    registry
        .addInterceptor(new TokenInterceptor(userRepository))
        .addPathPatterns("/api/v1/secured/**");
    registry
        .addInterceptor(new HeaderInterceptor())
        .addPathPatterns("/api/v1/**")
        .excludePathPatterns("/api/v1/public/verification/**");

  }

}
