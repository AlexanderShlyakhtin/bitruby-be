package kg.bitruby.usersservice.core.logic.registration;

import kg.bitruby.commonmodule.domain.AccountStatus;
import kg.bitruby.commonmodule.dto.kafkaevents.CommunicationType;
import kg.bitruby.commonmodule.dto.kafkaevents.OtpEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersservice.api.model.*;
import kg.bitruby.usersservice.core.event.NewUserRegistrationEvent;
import kg.bitruby.usersservice.core.event.UserCompleteRegistrationEvent;
import kg.bitruby.usersservice.core.services.users.UsersMapper;
import kg.bitruby.usersservice.core.services.utils.UtilsService;
import kg.bitruby.usersservice.outcomes.kafka.service.KafkaProducerService;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersservice.outcomes.postgres.repository.UserRepository;
import kg.bitruby.usersservice.outcomes.redis.domain.OtpRegistration;
import kg.bitruby.usersservice.outcomes.redis.domain.PreUserRegistration;
import kg.bitruby.usersservice.outcomes.redis.repository.OtpRegistrationRepository;
import kg.bitruby.usersservice.outcomes.redis.repository.PreUserRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher publisher;
  private final KafkaProducerService kafkaProducerService;
  private final PreUserRegistrationRepository preUserRegistrationRepository;
  private final OtpRegistrationRepository otpRegistrationRepository;
  private final UsersMapper usersMapper;
  private final UtilsService utils;


  @Transactional(transactionManager = "transactionManager")
  public RegisterNewUserResult registerUser(NewUser registerUser) {
    preRegistrationCheck(registerUser);
    UserEntity userEntity = new UserEntity();
    PreUserRegistration preRegistrationUser =
        usersMapper.map(registerUser, passwordEncoder.encode(registerUser.getPassword()));

    PreUserRegistration save = preUserRegistrationRepository.save(preRegistrationUser);
    publisher.publishEvent(new NewUserRegistrationEvent(userEntity));

    return new RegisterNewUserResult(true, OffsetDateTime.now(), UUID.fromString(save.getUuid()) );

  }


  @Transactional(transactionManager = "transactionManager")
  public Base generateOtpCodeForRegistration(GenerateOtpCodeRegistration otpCodeRegistration) {
    UserEntity userEntity =
        preUserRegistrationRepository.findById(otpCodeRegistration.getRegistrationId().toString())
            .map(usersMapper::toEntity)
            .orElseThrow(() -> new BitrubyRuntimeExpection("Retry registration"));
    checkUserForRegistration(userEntity);
    String code = utils.generateRandomCode();

    OtpRegistration token = new OtpRegistration();
    token.setId(otpCodeRegistration.getRegistrationId().toString());
    token.setUserId(otpCodeRegistration.getRegistrationId());
    token.setToken(code);
    token.setExpirationTime(utils.calculateExpirationDate());

    otpRegistrationRepository.save(token);
    kafkaProducerService.emitOtpRegistrationEventMessage(
        OtpEventDto.builder().code(code).sendTo(userEntity.getEmail()).communicationType(
            CommunicationType.EMAIL_PASSWORD).build());
    return new Base(true, OffsetDateTime.now());
  }


  @Transactional(transactionManager = "transactionManager")
  public Base completeRegistration(CompleteRegistration otpCodeCheck) {
    OtpRegistration
        token = otpRegistrationRepository.findById(otpCodeCheck.getRegistrationId().toString()).orElseThrow(() -> new BitrubyRuntimeExpection(
        "Token not valid"));
    if(!token.getToken().equals(otpCodeCheck.getOtp())) {
      throw new BitrubyRuntimeExpection("Token not valid");
    }
    PreUserRegistration preRegistration =
        preUserRegistrationRepository.findById( otpCodeCheck.getRegistrationId().toString() )
            .orElseThrow(() -> new BitrubyRuntimeExpection("Retry registration"));

    UserEntity userEntity = usersMapper.toEntity(preRegistration);
    userEntity.setEnabled(true);
    userEntity.setAccountStatus(AccountStatus.NOT_VERIFIED);
    userEntity.setEmailConfirmed(true);
    userRepository.save(userEntity);
    publisher.publishEvent(new UserCompleteRegistrationEvent(userEntity));

    return new Base(true, OffsetDateTime.now());
  }

  private void preRegistrationCheck(NewUser registerUser) {
    List<UserEntity> byPhoneAndIsEnabledTrue =
        userRepository.findByPhoneAndIsEnabledTrue(registerUser.getPhone());
    List<UserEntity> byEmailAndIsEnabledTrue =
        userRepository.findByEmailAndIsEnabledTrue(registerUser.getEmail());
    if(!byPhoneAndIsEnabledTrue.isEmpty() || !byEmailAndIsEnabledTrue.isEmpty()) {
      log.error("Такие номер телефона или электронная почта уже существуют у другого активного пользователя");
      throw new BitrubyRuntimeExpection("Пользователь не может быть зарегистрирован с указанными номером телефона и электронной почтой");
    }
  }

  private UserEntity checkUserForRegistration(UserEntity user) {
    if(user.isEnabled() || !user.getAccountStatus().equals(AccountStatus.REGISTRATION_STAGE)) {
      throw new BitrubyRuntimeExpection("Registration Error");
    }
    return user;
  }

}
