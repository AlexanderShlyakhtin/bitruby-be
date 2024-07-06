package kg.bitruby.usersservice.core.otp;

import kg.bitruby.commonmodule.domain.AccountStatus;
import kg.bitruby.commonmodule.dto.events.OtpEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersservice.api.model.*;
import kg.bitruby.usersservice.core.users.UsersMapper;
import kg.bitruby.usersservice.outcomes.kafka.service.KafkaProducerService;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersservice.outcomes.postgres.repository.UserRepository;
import kg.bitruby.usersservice.outcomes.redis.domain.OtpLogin;
import kg.bitruby.usersservice.outcomes.redis.domain.OtpRegistration;
import kg.bitruby.usersservice.outcomes.redis.domain.OtpRestorePassword;
import kg.bitruby.usersservice.outcomes.redis.repository.OtpLoginRepository;
import kg.bitruby.usersservice.outcomes.redis.repository.OtpRegistrationRepository;
import kg.bitruby.usersservice.outcomes.redis.repository.OtpRestorePasswordRepository;
import kg.bitruby.usersservice.outcomes.redis.repository.PreUserRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.*;

import static kg.bitruby.commonmodule.constants.AppConstants.TOKEN_NOT_VALID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
  private final UsersMapper usersMapper;
  private final OtpRestorePasswordRepository otpRestorePasswordRepository;
  private final OtpRegistrationRepository otpRegistrationRepository;
  private final UserRepository userRepository;
  private final OtpLoginRepository otpLoginRepository;
  private final PasswordEncoder passwordEncoder;
  private final KafkaProducerService kafkaProducerService;
  private final PreUserRegistrationRepository preUserRegistrationRepository;

  private static final String CHARACTERS = "0123456789";
  private static final int EXPIRATION_TIME = 30;

  private static final Random RANDOM = new SecureRandom();

  @Transactional(transactionManager = "transactionManager")
  public GenerateOtpCodeLoginResult generateOtpCodeForLogin(OtpCodeLogin otpCode) {
    UserEntity userEntity;
    String sendTo = otpCode.getSendTo();
    userEntity = checkUserWithTokenAndReturn(otpCode.getGrantType(), sendTo);
    if(!passwordEncoder.matches(otpCode.getPassword(), userEntity.getPassword())) {
      throw new BitrubyRuntimeExpection("Неверная пара логин/пароль");
    }
    String code = generateRandomCode();
    OtpLogin otpLogin = new OtpLogin();
    otpLogin.setId(UUID.randomUUID().toString());
    otpLogin.setUserId(userEntity.getId());
    otpLogin.setExpirationTime(calculateExpirationDate(EXPIRATION_TIME));
    otpLogin.setToken(code);

    otpLoginRepository.save(otpLogin);

    kafkaProducerService.emitOtpLoginEventMessage(OtpEventDto.builder().code(code).sendTo(sendTo).grantType(otpCode.getGrantType()).build());
    return new GenerateOtpCodeLoginResult(true, OffsetDateTime.now(), UUID.fromString(otpLogin.getId()) );
  }

  @Transactional(transactionManager = "transactionManager")
  public Base generateOtpCodeForRegistration(GenerateOtpCodeRegistration otpCodeRegistration) {
    UserEntity userEntity =
        preUserRegistrationRepository.findById(otpCodeRegistration.getRegistrationId().toString())
            .map(usersMapper::toEntity)
            .orElseThrow(() -> new BitrubyRuntimeExpection("Retry registration"));
    checkUserForRegistration(userEntity);
    String code = generateRandomCode();

    OtpRegistration token = new OtpRegistration();
    token.setId(otpCodeRegistration.getRegistrationId().toString());
    token.setUserId(otpCodeRegistration.getRegistrationId());
    token.setToken(code);
    token.setExpirationTime(calculateExpirationDate(EXPIRATION_TIME));

    otpRegistrationRepository.save(token);
    kafkaProducerService.emitOtpRegistrationEventMessage(OtpEventDto.builder().code(code).sendTo(userEntity.getEmail()).grantType(GrantType.EMAIL_PASSWORD).build());
    return new Base(true, OffsetDateTime.now());
  }

  @Transactional(transactionManager = "transactionManager")
  public RestorePasswordRequestOtpResult generateOtpCodeForRestoringPassword(OtpCode otpCode) {
    String sendTo = otpCode.getSendTo();
    UserEntity userEntity = checkUserWithTokenAndReturn(otpCode.getGrantType(), otpCode.getSendTo());
    checkUserWithTokenForRestorePassword(userEntity);

    String code = generateRandomCode();
    OtpRestorePassword otpRestorePassword = new OtpRestorePassword();
    otpRestorePassword.setId(UUID.randomUUID().toString());
    otpRestorePassword.setUserId(userEntity.getId());
    otpRestorePassword.setExpirationTime(calculateExpirationDate(EXPIRATION_TIME));
    otpRestorePassword.setToken(code);
    otpRestorePasswordRepository.save(otpRestorePassword);
    kafkaProducerService.emitOtpRestorePasswordEventMessage(OtpEventDto.builder().code(code).sendTo(sendTo).grantType(otpCode.getGrantType()).build());
    return new RestorePasswordRequestOtpResult(true, OffsetDateTime.now(), UUID.fromString(otpRestorePassword.getId()));
  }

  @Transactional(transactionManager = "transactionManager")
  public Base checkOtpCodeForRestoringPassword(OtpCodeRestorePassword otpCodeRestorePassword) {
    OtpRestorePassword token = otpRestorePasswordRepository.findById(otpCodeRestorePassword.getRestorePasswordId().toString()).orElseThrow(() -> new BitrubyRuntimeExpection(TOKEN_NOT_VALID));
    if(!token.getToken().equals(otpCodeRestorePassword.getOtp()) || !new Date().before(token.getExpirationTime())) {
      throw new BitrubyRuntimeExpection(TOKEN_NOT_VALID);
    }
    UserEntity userEntity = findUserById(token.getUserId());
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

  private UserEntity findUserById(UUID userId) {
    return userRepository.findById(userId).orElseThrow(() -> new BitrubyRuntimeExpection(
        String.format("User with id: %s not found for the verification event", userId)));
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

  private UserEntity checkUserForRegistration(UserEntity user) {
    if(user.isEnabled() || !user.getAccountStatus().equals(AccountStatus.REGISTRATION_STAGE)) {
      throw new BitrubyRuntimeExpection("Registration Error");
    }
    return user;
  }

  private String generateRandomCode() {
    StringBuilder sb = new StringBuilder(6);
    for (int i = 0; i < 6; i++) {
      int randomIndex = RANDOM.nextInt(CHARACTERS.length());
      sb.append(CHARACTERS.charAt(randomIndex));
    }
    return sb.toString();
  }

  private Date calculateExpirationDate(int expirationTime) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(new Date().getTime());
    calendar.add(Calendar.MINUTE, expirationTime);
    return new Date(calendar.getTime().getTime());
  }

}
