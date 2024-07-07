package kg.bitruby.usersservice.core.login;

import kg.bitruby.commonmodule.dto.events.OtpEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersservice.api.model.GenerateOtpCodeLoginResult;
import kg.bitruby.usersservice.api.model.OtpCodeLogin;
import kg.bitruby.usersservice.core.users.UsersService;
import kg.bitruby.usersservice.core.utils.Utils;
import kg.bitruby.usersservice.outcomes.kafka.service.KafkaProducerService;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersservice.outcomes.redis.domain.OtpLogin;
import kg.bitruby.usersservice.outcomes.redis.repository.OtpLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {

  private final PasswordEncoder passwordEncoder;
  private final Utils utils;
  private final UsersService usersService;
  private final OtpLoginRepository otpLoginRepository;
  private final KafkaProducerService kafkaProducerService;


  @Transactional(transactionManager = "transactionManager")
  public GenerateOtpCodeLoginResult generateOtpCodeForLogin(OtpCodeLogin otpCode) {
    UserEntity userEntity;
    String sendTo = otpCode.getSendTo();
    userEntity = usersService.checkUserWithTokenAndReturn(otpCode.getGrantType(), sendTo);
    if(!passwordEncoder.matches(otpCode.getPassword(), userEntity.getPassword())) {
      throw new BitrubyRuntimeExpection("Неверная пара логин/пароль");
    }
    String code = utils.generateRandomCode();
    OtpLogin otpLogin = new OtpLogin();
    otpLogin.setId(UUID.randomUUID().toString());
    otpLogin.setUserId(userEntity.getId());
    otpLogin.setExpirationTime(utils.calculateExpirationDate());
    otpLogin.setToken(code);

    otpLoginRepository.save(otpLogin);

    kafkaProducerService.emitOtpLoginEventMessage(
        OtpEventDto.builder().code(code).sendTo(sendTo).grantType(otpCode.getGrantType()).build());
    return new GenerateOtpCodeLoginResult(true, OffsetDateTime.now(), UUID.fromString(otpLogin.getId()) );
  }


}
