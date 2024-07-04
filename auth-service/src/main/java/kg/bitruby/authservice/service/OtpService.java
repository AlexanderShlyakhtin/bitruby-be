package kg.bitruby.authservice.service;

import kg.bitruby.authservice.entity.OtpLoginTokenEntity;
import kg.bitruby.authservice.repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class OtpService {

  private final OtpTokenRepository otpTokenRepository;

  public boolean checkAndUseOtpCode(String key, String otpToCheck) {
    OtpLoginTokenEntity token =
        otpTokenRepository.findById(key).orElseThrow(() -> new OAuth2AuthenticationException(
            "Token not valid"));
    if(!Objects.equals(token.getToken(), otpToCheck) || !token.getExpirationTime().toInstant().isAfter(new Date().toInstant())) {
      return false;
    } else {
      otpTokenRepository.deleteById(key);
      return true;
    }
  }

}
