package kg.bitruby.usersapp.incomes.rest.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersapp.api.model.VerificationDecision;
import kg.bitruby.usersapp.api.model.VerificationEvent;
import kg.bitruby.usersapp.core.verification.HmacVerifier;
import kg.bitruby.usersapp.core.verification.VerificationService;
import kg.bitruby.usersapp.incomes.rest.config.CachedBodyHttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckHMacSignatureInterceptor implements HandlerInterceptor {

  private final HmacVerifier hmacVerifier;
  private final VerificationService verificationService;
  private final ObjectMapper objectMapper;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String xHmacSignature = request.getHeader("X-Hmac-Signature");

    // Wrap the original request with ContentCachingRequestWrapper to cache the body
    try {
      CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
      String payload = getRequestBody(cachedBodyHttpServletRequest);
      boolean signatureValid = hmacVerifier.isSignatureValid(xHmacSignature, payload);
      if (signatureValid) {
        String requestURI = request.getRequestURI();
        if(requestURI.equals("/users/api/v1/public/verification/event")) {
          verificationService.verificationEvent(mapObject(payload, VerificationEvent.class));
        } else if(requestURI.equals("/users/api/v1/public/verification/decision")) {
          verificationService.verificationDecision(mapObject(payload, VerificationDecision.class));
        }
        return true;
      }
    } catch (Exception exception) {
      throw new BitrubyRuntimeExpection("Can't extract request body for verification webhook", exception);
    }

    log.error("Received Veriff Webhook with not valid signature");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    return false;
  }

  private String getRequestBody(CachedBodyHttpServletRequest request) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      sb.append(line);
    }
    return sb.toString();
  }

  public <T> T mapObject(String sourceObject, Class<T> targetClass) {
    try {
      return objectMapper.readValue(sourceObject, targetClass);
    } catch (Exception e) {
      throw new BitrubyRuntimeExpection("Error. Can't convert source file into target class");
    }
  }

}
