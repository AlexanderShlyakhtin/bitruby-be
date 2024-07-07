package kg.bitruby.authservice.core;

import kg.bitruby.authservice.api.model.OtpLogin;
import kg.bitruby.authservice.api.model.OtpLoginResult;
import kg.bitruby.authservice.core.utils.Utils;
import kg.bitruby.authservice.outcomes.kafka.service.KafkaProducerService;
import kg.bitruby.authservice.outcomes.postgres.entity.UserEntity;
import kg.bitruby.authservice.outcomes.redis.domain.OtpLoginEntity;
import kg.bitruby.authservice.outcomes.redis.repository.OtpLoginRepository;
import kg.bitruby.commonmodule.dto.kafkaevents.CommunicationType;
import kg.bitruby.commonmodule.dto.kafkaevents.OtpEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {

  private final OtpLoginRepository otpLoginRepository;
  private final PasswordEncoder passwordEncoder;
  private final Utils utils;
  private final UsersService usersService;
  private final KafkaProducerService kafkaProducerService;

  @Transactional(transactionManager = "transactionManager")
  public boolean checkAndUseOtpCode(UserEntity userEntity, String otpToCheck, UUID loginId) {
    OtpLoginEntity token = otpLoginRepository.findById(loginId.toString()).orElseThrow(() -> new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_TOKEN));
    if(!token.getToken().equals(otpToCheck) || !(new Date().before(token.getExpirationTime())) || !token.getUserId().equals(userEntity.getId())) {
      return false;
    } else {
      otpLoginRepository.deleteById(token.getId().toString());
      return true;
    }
  }

  @Transactional(transactionManager = "transactionManager")
  public OtpLoginResult generateOtpCodeForLogin(OtpLogin otpCode) {
    UserEntity userEntity;
    String sendTo = otpCode.getSendTo();
    userEntity = usersService.checkUserWithTokenAndReturn(otpCode.getGrantType(), sendTo);
    if(!passwordEncoder.matches(otpCode.getPassword(), userEntity.getPassword())) {
      throw new BitrubyRuntimeExpection("Неверная пара логин/пароль");
    }
    String code = utils.generateRandomCode();
    OtpLoginEntity otpLogin = new OtpLoginEntity();
    otpLogin.setId(UUID.randomUUID().toString());
    otpLogin.setUserId(userEntity.getId());
    otpLogin.setExpirationTime(utils.calculateExpirationDate());
    otpLogin.setToken(code);

    otpLoginRepository.save(otpLogin);

    kafkaProducerService.emitOtpLoginEventMessage(
        OtpEventDto.builder().code(code).sendTo(sendTo).communicationType(
            CommunicationType.fromValue(otpCode.getGrantType().getValue())).build());
    return new OtpLoginResult(true, OffsetDateTime.now(), UUID.fromString(otpLogin.getId()) );
  }

}
