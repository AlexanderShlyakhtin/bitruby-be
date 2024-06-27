package kg.bitruby.usersapp.incomes.rest.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import kg.bitruby.usersapp.common.AppContextHolder;
import kg.bitruby.usersapp.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersapp.outcomes.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

  private final UserRepository userRepository;

  @Override
  public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Jwt principal = (Jwt) authentication.getPrincipal();
    if(principal.getClaims().get("sub") != null) {
      String sub = principal.getClaims().get("sub").toString();
      UserEntity userEntity = userRepository.findByEmail(sub).orElseThrow(
          () -> new RuntimeException(String.format("User with email: %s not found", sub)));
      AppContextHolder.setUserId(userEntity.getId());
    return true;
    } else return false;
  }

  @Override
  public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
    AppContextHolder.cleanContext();
  }

}
