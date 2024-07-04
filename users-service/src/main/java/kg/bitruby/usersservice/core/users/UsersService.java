package kg.bitruby.usersservice.core.users;

import kg.bitruby.commonmodule.domain.AccountStatus;
import kg.bitruby.commonmodule.domain.AuthorityRoleEnum;
import kg.bitruby.commonmodule.dto.events.CreateSubAccountDto;
import kg.bitruby.commonmodule.dto.events.VerificationDecisionDto;
import kg.bitruby.commonmodule.dto.events.VerificationDecisionStatus;
import kg.bitruby.commonmodule.dto.events.VerificationEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersservice.api.model.*;
import kg.bitruby.usersservice.client.veriff.api.model.NewSession;
import kg.bitruby.usersservice.client.veriff.api.model.NewSessionVerification;
import kg.bitruby.usersservice.client.veriff.api.model.NewSessionVerificationAddress;
import kg.bitruby.usersservice.client.veriff.api.model.Session;
import kg.bitruby.usersservice.common.AppContextHolder;
import kg.bitruby.usersservice.core.event.NewUserRegistrationEvent;
import kg.bitruby.usersservice.core.event.UserCompleteRegistrationEvent;
import kg.bitruby.usersservice.core.event.UserVerificationDecisionEvent;
import kg.bitruby.usersservice.outcomes.kafka.service.KafkaProducerService;
import kg.bitruby.usersservice.outcomes.postgres.domain.OtpRegistrationTokenEntity;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersservice.outcomes.postgres.domain.UsersVerificationSessions;
import kg.bitruby.usersservice.outcomes.postgres.domain.VerificationSessionStatus;
import kg.bitruby.usersservice.outcomes.postgres.repository.OtpRegistrationTokenRepository;
import kg.bitruby.usersservice.outcomes.postgres.repository.UserRepository;
import kg.bitruby.usersservice.outcomes.postgres.repository.UsersVerificationSessionsRepository;
import kg.bitruby.usersservice.outcomes.rest.veriff.api.VeriffApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher publisher;
  private final OtpRegistrationTokenRepository otpTokenRepository;
  private final UsersVerificationSessionsRepository verificationSessionsRepository;
  private final VeriffApiClient veriffApiClient;
  private final KafkaProducerService kafkaProducerService;

  @Value("${bitruby.verification.callback-url}")
  private String callbackUrl;

  @Transactional
  public Base registerUser(NewUser registerUser) {
    userRepository.findByEmail(registerUser.getEmail()).ifPresent(user -> {throw new BitrubyRuntimeExpection("Email already registered");});

    preRegistrationCheck(registerUser);

    UserEntity userEntity = new UserEntity();
    userEntity.setPhone(registerUser.getPhone());
    userEntity.setEmail(registerUser.getEmail());
    userEntity.setPassword(passwordEncoder.encode(registerUser.getPassword()));
    userEntity.setEnabled(false);
    userEntity.setEmailConfirmed(false);
    userEntity.setPhoneConfirmed(false);
    userEntity.setAccountStatus(AccountStatus.REGISTRATION_STAGE);
    userEntity.setRole(AuthorityRoleEnum.USER);
    userRepository.save(userEntity);
    publisher.publishEvent(new NewUserRegistrationEvent(userEntity));

    return new Base(true, OffsetDateTime.now());

  }

  @Transactional
  public Base completeRegistration(OtpCodeCheck otpCodeCheck) {
    checkToken(otpCodeCheck);
    UserEntity userEntity = getUserEntityByGrantType(otpCodeCheck.getGrantType(), otpCodeCheck.getSendTo());
    userEntity.setEnabled(true);
    userEntity.setAccountStatus(AccountStatus.NOT_VERIFIED);
    userEntity.setEmailConfirmed(true);
    userRepository.save(userEntity);
    publisher.publishEvent(new UserCompleteRegistrationEvent(userEntity));

    return new Base(true, OffsetDateTime.now());
  }

  private UserEntity getUserEntityByGrantType(GrantType grantType, String sendTo) {
    UserEntity userEntity;
    if(grantType.equals(GrantType.EMAIL_PASSWORD)) {
      userEntity = userRepository.findByEmail(sendTo)
          .orElseThrow(() -> new BitrubyRuntimeExpection("Token is not valid"));
    } else if(grantType.equals(GrantType.PHONE_PASSWORD)) {
      userEntity = userRepository.findByPhone(sendTo)
          .orElseThrow(() -> new BitrubyRuntimeExpection("Token is not valid"));
    } else throw new BitrubyRuntimeExpection("Unknown grant type");
    return userEntity;
  }

  private void checkToken(OtpCodeCheck otpCodeCheck) {
    OtpRegistrationTokenEntity token =
        otpTokenRepository.findById(otpCodeCheck.getSendTo()).orElseThrow(() -> new RuntimeException("Token not found"));
    if( Objects.equals(token.getToken(), otpCodeCheck.getOtp()) && !new Date().toInstant().isAfter(token.getExpirationTime().toInstant()) ) {
      otpTokenRepository.deleteById(otpCodeCheck.getSendTo());
    } else throw new BitrubyRuntimeExpection("Token is not valid");
  }

  public void handleVerificationEvent(VerificationEventDto event) {
    log.info("Receive VerificationEvent: {}", event.toString());
    UsersVerificationSessions verificationSessions =
        verificationSessionsRepository.findByIdAndActiveTrue(event.getId()).orElseThrow(
            () -> new BitrubyRuntimeExpection(
                String.format("User verification session with id: %s not found ",
                    AppContextHolder.getContextRequestId())));
    switch (event.getAction()) {
      case STARTED -> {
        verificationSessions.setStatus(VerificationSessionStatus.STARTED);
        verificationSessions.setUpdated(OffsetDateTime.now());
        verificationSessionsRepository.save(verificationSessions);
      }
      case SUBMITTED -> {
        verificationSessions.setStatus(VerificationSessionStatus.WAITING_FOR_REVIEW);
        verificationSessions.setUpdated(OffsetDateTime.now());
        verificationSessionsRepository.save(verificationSessions);
      }
      default -> log.info("Unhandled session event status action: {}", event.getAction().getValue());
    }

  }

  public void handleVerificationDecision(VerificationDecisionDto event) {
    UserEntity userEntity = findUserById(event.getUserId());
    UsersVerificationSessions verificationSessions =
        verificationSessionsRepository.findById(AppContextHolder.getContextRequestId()).orElseThrow(
            () -> new BitrubyRuntimeExpection(
                String.format("User verification session with id: %s not found ",
                    AppContextHolder.getContextRequestId())));
    switch (event.getStatus()) {
      case APPROVED -> {
        userEntity.setAccountStatus(AccountStatus.ACCOUNT_NOT_CREATED);
        userRepository.save(userEntity);
        verificationSessions.setActive(false);
        verificationSessionsRepository.save(verificationSessions);
        verificationSessions.setStatus(VerificationSessionStatus.SUCCESS);
        verificationSessions.setUpdated(OffsetDateTime.now());

        kafkaProducerService.emitCreateSubAccountMessage(CreateSubAccountDto.builder()
                .userId(userEntity.getId()).build());
        publisher.publishEvent(new UserVerificationDecisionEvent(userEntity, VerificationDecisionStatus.APPROVED));

      }
      case DECLINED -> {
        verificationSessions.setActive(false);
        verificationSessions.setStatus(VerificationSessionStatus.REJECTED);
        verificationSessionsRepository.save(verificationSessions);
        verificationSessions.setUpdated(OffsetDateTime.now());
        publisher.publishEvent(new UserVerificationDecisionEvent(userEntity, VerificationDecisionStatus.DECLINED));
      }

      case RESUBMISSION_REQUESTED -> {
        //TODO: Make session not active on 9th attempt
        verificationSessions.setStatus(VerificationSessionStatus.WAITING_FOR_RESUBMISSION);
        verificationSessions.setUpdated(OffsetDateTime.now());
        publisher.publishEvent(new UserVerificationDecisionEvent(userEntity, VerificationDecisionStatus.RESUBMISSION_REQUESTED));
      }

      default -> log.info("Verification not handle with status {}", event.getStatus().getValue());
    }

  }

  private UserEntity findUserById(UUID userId) {
    return userRepository.findById(userId).orElseThrow(() -> new BitrubyRuntimeExpection(
        String.format("User with id: %s not found for the verification event", userId)));
  }

  @Transactional
  public Base applyUserForm(UserForm userForm) {
    UUID userId = AppContextHolder.getContextUserId();

    UserEntity userEntity = findUserById(userId);
    userEntity.setFirstName(userForm.getFirstName());
    userEntity.setLastName(userForm.getLastName());
    userEntity.setAddress(userForm.getAddress());
    userRepository.save(userEntity);

    Optional<UsersVerificationSessions> optionalActiveSession =
        verificationSessionsRepository.findByUserId_IdAndActiveTrue(userId);
    if(optionalActiveSession.isEmpty()) {
      NewSessionVerification newSessionVerification = mapVerification(userId, userForm);
      NewSession newSession = new NewSession();
      newSession.setVerification(newSessionVerification);
      Session session = veriffApiClient.createSession(newSession);

      UsersVerificationSessions verificationSessions = new UsersVerificationSessions();
      verificationSessions.setId(session.getVerification().getId());
      verificationSessions.setSessionUrl(session.getVerification().getUrl());
      verificationSessions.setActive(true);
      verificationSessions.setCreated(OffsetDateTime.now());
      verificationSessions.setUpdated(OffsetDateTime.now());
      verificationSessions.setUserId(userEntity);
      verificationSessions.setStatus(VerificationSessionStatus.WAITING_FOR_START);
      verificationSessionsRepository.save(verificationSessions);
    }

    return new Base(true, OffsetDateTime.now());
  }

  public UserVerification getUserVerificationData() {
    UUID userId = AppContextHolder.getContextUserId();

    UserVerification userVerification = new UserVerification();
    Optional<UsersVerificationSessions> optionalVerificationSessions =
        verificationSessionsRepository.findByUserId_IdAndActiveTrue(userId);
    if(optionalVerificationSessions.isPresent()) {
      UsersVerificationSessions verificationSessions = optionalVerificationSessions.get();
      userVerification.setVerificationSession(JsonNullable.of(new UserVerificationAllOfVerificationSession(verificationSessions.getSessionUrl(), VerificationStatus.fromValue(verificationSessions.getStatus().getValue()))));
      UserEntity userEntity = verificationSessions.getUserId();
      userVerification.setUser(new UserForm(userEntity.getFirstName(), userEntity.getLastName(), userEntity.getAddress()));
    }
    userVerification.setSuccess(true);
    userVerification.setTimestamp(OffsetDateTime.now());

    return userVerification;
  }

  private NewSessionVerification mapVerification(UUID userId, UserForm userForm) {
    NewSessionVerification newSessionVerification = new NewSessionVerification();

    kg.bitruby.usersservice.client.veriff.api.model.Person person =
        new kg.bitruby.usersservice.client.veriff.api.model.Person();
    person.setFirstName(userForm.getFirstName());
    person.setLastName(userForm.getLastName());
    person.setIdNumber(userId);
    newSessionVerification.setPerson(person);

    NewSessionVerificationAddress newSessionVerificationAddress =
        new NewSessionVerificationAddress();
    newSessionVerificationAddress.setFullAddress(userForm.getAddress());
    newSessionVerification.setAddress(newSessionVerificationAddress);
    newSessionVerification.setCallback(callbackUrl);
    return newSessionVerification;
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


}