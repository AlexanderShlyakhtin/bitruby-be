package kg.bitruby.usersservice.core.restorepassword;

import kg.bitruby.commonmodule.dto.events.OtpEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersservice.api.model.*;
import kg.bitruby.usersservice.core.users.UsersService;
import kg.bitruby.usersservice.core.utils.Utils;
import kg.bitruby.usersservice.outcomes.kafka.service.KafkaProducerService;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersservice.outcomes.redis.domain.OtpRestorePassword;
import kg.bitruby.usersservice.outcomes.redis.repository.OtpRestorePasswordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

import static kg.bitruby.commonmodule.constants.AppConstants.TOKEN_NOT_VALID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestorePasswordService {

  private final UsersService usersService;
  private final PasswordEncoder passwordEncoder;
  private final Utils utils;
  private final OtpRestorePasswordRepository otpRestorePasswordRepository;
  private final KafkaProducerService kafkaProducerService;

  public Base restorePassword(RestorePassword restorePassword) {
    OtpRestorePassword otpRestorePassword = otpRestorePasswordRepository.findById(restorePassword.getRestorePasswordId().toString()).orElseThrow(() -> new BitrubyRuntimeExpection(TOKEN_NOT_VALID));
    UserEntity userEntity = usersService.findUserById(otpRestorePassword.getUserId());

    checkUserWithTokenForRestorePassword(userEntity);
    if (passwordEncoder.matches(restorePassword.getPassword(), userEntity.getPassword())) {
      throw new BitrubyRuntimeExpection("Новый пароль не должен совпадать со старым");
    }
    userEntity.setPassword(passwordEncoder.encode(restorePassword.getPassword()));
    usersService.save(userEntity);
    return new Base(true, OffsetDateTime.now());
  }

  @Transactional(transactionManager = "transactionManager")
  public RestorePasswordRequestOtpResult generateOtpCodeForRestoringPassword(OtpCode otpCode) {
    String sendTo = otpCode.getSendTo();
    UserEntity userEntity = usersService.checkUserWithTokenAndReturn(otpCode.getGrantType(), otpCode.getSendTo());
    checkUserWithTokenForRestorePassword(userEntity);

    String code = utils.generateRandomCode();
    OtpRestorePassword otpRestorePassword = new OtpRestorePassword();
    otpRestorePassword.setId(UUID.randomUUID().toString());
    otpRestorePassword.setUserId(userEntity.getId());
    otpRestorePassword.setExpirationTime(utils.calculateExpirationDate());
    otpRestorePassword.setToken(code);
    otpRestorePasswordRepository.save(otpRestorePassword);
    kafkaProducerService.emitOtpRestorePasswordEventMessage(
        OtpEventDto.builder().code(code).sendTo(sendTo).grantType(otpCode.getGrantType()).build());
    return new RestorePasswordRequestOtpResult(true, OffsetDateTime.now(), UUID.fromString(otpRestorePassword.getId()));
  }

  @Transactional(transactionManager = "transactionManager")
  public Base checkOtpCodeForRestoringPassword(OtpCodeRestorePassword otpCodeRestorePassword) {
    OtpRestorePassword token = otpRestorePasswordRepository.findById(otpCodeRestorePassword.getRestorePasswordId().toString()).orElseThrow(() -> new BitrubyRuntimeExpection(TOKEN_NOT_VALID));
    if(!token.getToken().equals(otpCodeRestorePassword.getOtp()) || !new Date().before(token.getExpirationTime())) {
      throw new BitrubyRuntimeExpection(TOKEN_NOT_VALID);
    }
    UserEntity userEntity = usersService.findUserById(token.getUserId());
    checkUserWithTokenForRestorePassword(userEntity);

    return new Base(true, OffsetDateTime.now());
  }

  private void checkUserWithTokenForRestorePassword(UserEntity userEntity) {
    if (!userEntity.isEnabled()) {
      log.error("Попытка восстановить пароль для удаленного пользователя");
      throw new BitrubyRuntimeExpection("Пользователь не существует");
    }
    switch (userEntity.getAccountStatus()) {
      case ACCOUNT_LOCK -> {
        log.error("Попытка восстановить пароль для заблокированного пользователя");
        throw new BitrubyRuntimeExpection("Пользователь заблокирован");
      }
      case REGISTRATION_STAGE -> {
        log.error("Попытка восстановить пароль для не зарегистрированного пользователя");
        throw new BitrubyRuntimeExpection("Пользователь не существует");
      }
    }
  }


}
