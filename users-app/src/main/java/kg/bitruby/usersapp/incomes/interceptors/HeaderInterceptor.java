package kg.bitruby.usersapp.incomes.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import kg.bitruby.usersapp.common.AppContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HeaderInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
    UUID rqUid = UUID.fromString(request.getHeader("x-request-id"));
    AppContextHolder.setContext(rqUid);
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    response.addHeader("x-request-id", AppContextHolder.getContextRequestId().toString());
  }

  @Override
  public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
    AppContextHolder.cleanContext();
  }

}
