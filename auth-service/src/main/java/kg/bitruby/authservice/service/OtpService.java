package kg.bitruby.authservice.service;

import kg.bitruby.authservice.outcomes.postgres.entity.UserEntity;
import kg.bitruby.authservice.outcomes.redis.domain.OtpLogin;
import kg.bitruby.authservice.outcomes.redis.repository.OtpLoginRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class OtpService {

  private final OtpLoginRepository otpLoginRepository;

  public boolean checkAndUseOtpCode(UserEntity userEntity, String otpToCheck, UUID loginId) {
    OtpLogin token = otpLoginRepository.findById(loginId)
        .orElseThrow(() -> new OAuth2AuthenticationException(OAuth2ErrorCodes.ACCESS_DENIED));
    if(!token.getToken().equals(otpToCheck) || !token.getExpirationTime().before(new Date()) || !token.getUserId().equals(userEntity.getId())) {
      return false;
    } else {
      otpLoginRepository.deleteById(token.getId());
      return true;
    }
  }

}
