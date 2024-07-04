package kg.bitruby.usersservice.core.otp;

import kg.bitruby.commonmodule.dto.events.OtpEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersservice.api.model.*;
import kg.bitruby.usersservice.outcomes.kafka.service.KafkaProducerService;
import kg.bitruby.usersservice.outcomes.postgres.domain.OtpLoginTokenEntity;
import kg.bitruby.usersservice.outcomes.postgres.domain.OtpRegistrationTokenEntity;
import kg.bitruby.usersservice.outcomes.postgres.domain.OtpRestorePasswordTokenEntity;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersservice.outcomes.postgres.repository.OtpLoginTokenRepository;
import kg.bitruby.usersservice.outcomes.postgres.repository.OtpRegistrationTokenRepository;
import kg.bitruby.usersservice.outcomes.postgres.repository.OtpRestorePasswordTokenEntityRepository;
import kg.bitruby.usersservice.outcomes.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
  private final OtpRestorePasswordTokenEntityRepository otpRestorePasswordTokenEntityRepository;
  private final OtpRegistrationTokenRepository otpRegistrationTokenEntityRepository;
  private final UserRepository userRepository;
  private final OtpLoginTokenRepository otpLoginTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final KafkaProducerService kafkaProducerService;

  private static final String CHARACTERS = "0123456789";
  private static final Random RANDOM = new SecureRandom();

  @Transactional(transactionManager = "transactionManager")
  public Base generateOtpCodeForLogin(OtpCodeLogin otpCode) {
    UserEntity userEntity;
    String sendTo = otpCode.getSendTo();
    userEntity = checkUserWithTokenAndReturn(GrantType.EMAIL_PASSWORD, sendTo);
    if(!passwordEncoder.matches(otpCode.getPassword(), userEntity.getPassword())) {
      throw new BitrubyRuntimeExpection("Неверная пара логин/пароль");
    }
    String code = generateRandomCode();
    kafkaProducerService.emitOtpLoginEventMessage(OtpEventDto.builder().code(code).sendTo(sendTo).grantType(otpCode.getGrantType()).build());
    otpLoginTokenRepository.save(new OtpLoginTokenEntity(userEntity, code, true));
    return new Base(true, OffsetDateTime.now());
  }

  @Transactional(transactionManager = "transactionManager")
  public Base generateOtpCodeForRegistration(OtpCode otpCode) {
    String sendTo = otpCode.getSendTo();
    UserEntity userEntity = checkUserWithTokenAndReturn(otpCode.getGrantType(), sendTo);
    String code = generateRandomCode();
    kafkaProducerService.emitOtpRegistrationEventMessage(OtpEventDto.builder().code(code).sendTo(sendTo).grantType(otpCode.getGrantType()).build());
    otpRegistrationTokenEntityRepository.save(new OtpRegistrationTokenEntity(userEntity, code, true));
    return new Base(true, OffsetDateTime.now());
  }

  @Transactional(transactionManager = "transactionManager")
  public Base generateOtpCodeForRestoringPassword(OtpCode otpCode) {
    String sendTo = otpCode.getSendTo();
    UserEntity userEntity = checkUserWithTokenForRestorePassword(otpCode.getGrantType(), sendTo);
    String code = generateRandomCode();
    kafkaProducerService.emitOtpRestorePasswordEventMessage(OtpEventDto.builder().code(code).sendTo(sendTo).grantType(otpCode.getGrantType()).build());
    otpRestorePasswordTokenEntityRepository.save(new OtpRestorePasswordTokenEntity(userEntity, code, true));
    return new Base(true, OffsetDateTime.now());
  }

  @Transactional
  public Base checkOtpCodeForRestoringPassword(OtpCodeRestorePassword otpCodeRestorePassword) {
    String sendTo = otpCodeRestorePassword.getSendTo();
    checkUserWithTokenForRestorePassword(otpCodeRestorePassword.getGrantType(), sendTo);
    OtpRestorePasswordTokenEntity token =
        otpRestorePasswordTokenEntityRepository.findByToken(otpCodeRestorePassword.getOtp())
            .orElseThrow(() -> new BitrubyRuntimeExpection("OTP токен не валидный"));
    if(!token.getExpirationTime().after(new Date())) {
      throw new BitrubyRuntimeExpection("OTP токен не валидный");
    }
    return new Base(true, OffsetDateTime.now());
  }

  public void checkToken(OtpCodeCheck otpCodeCheck) {
    UserEntity userEntity =
        checkUserWithTokenAndReturn(otpCodeCheck.getGrantType(), otpCodeCheck.getSendTo());
    OtpRegistrationTokenEntity token = otpRegistrationTokenEntityRepository.findById(userEntity)
        .orElseThrow(() -> new RuntimeException("Token not found"));
    if( Objects.equals(token.getToken(), otpCodeCheck.getOtp()) && !new Date().toInstant().isAfter(token.getExpirationTime().toInstant()) ) {
      otpRegistrationTokenEntityRepository.deleteById(userEntity);
    } else throw new BitrubyRuntimeExpection("Token is not valid");
  }


  private UserEntity checkUserWithTokenForRestorePassword(GrantType grantType, String sendTo) {
    UserEntity userEntity;
    if (grantType.equals(GrantType.EMAIL_PASSWORD)) {
      userEntity = userRepository.findByEmail(sendTo).orElseThrow(() -> new BitrubyRuntimeExpection("User not exists"));
    } else if (grantType.equals(GrantType.PHONE_PASSWORD)) {
      userEntity = userRepository.findByPhone(sendTo).orElseThrow(() -> new BitrubyRuntimeExpection("User not exists"));
    } else
      throw new BitrubyRuntimeExpection("Unknown Grant type");
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
      default -> {
        return userEntity;
      }
    }
  }

  private UserEntity checkUserWithTokenAndReturn(GrantType grantType, String sendTo) {
    UserEntity userEntity;
    if(grantType.equals(GrantType.EMAIL_PASSWORD)) {
      List<UserEntity> userEntityList = userRepository.findByEmailAndIsEnabledTrue(sendTo);
      if(userEntityList.size() != 1) throw new BitrubyRuntimeExpection("User not exists");
      userEntity = userEntityList.stream().findFirst().get();
    } else if (grantType.equals(GrantType.PHONE_PASSWORD)) {
      List<UserEntity> userEntityList = userRepository.findByPhoneAndIsEnabledTrue(sendTo);
      if(userEntityList.size() != 1) throw new BitrubyRuntimeExpection("User not exists");
      userEntity = userEntityList.stream().findFirst().get();
    } else throw new BitrubyRuntimeExpection("Unknown Grant type");

    return userEntity;
  }

  private String generateRandomCode() {
    StringBuilder sb = new StringBuilder(6);
    for (int i = 0; i < 6; i++) {
      int randomIndex = RANDOM.nextInt(CHARACTERS.length());
      sb.append(CHARACTERS.charAt(randomIndex));
    }
    return sb.toString();
  }

}
