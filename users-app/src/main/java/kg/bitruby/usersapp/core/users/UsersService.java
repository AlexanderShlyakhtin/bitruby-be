package kg.bitruby.usersapp.core.users;

import kg.bitruby.commonmodule.dto.eventDto.CreateSubAccountDto;
import kg.bitruby.commonmodule.dto.eventDto.VerificationDecisionDto;
import kg.bitruby.commonmodule.dto.eventDto.VerificationDecisionStatus;
import kg.bitruby.commonmodule.dto.eventDto.VerificationEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersapp.api.model.*;
import kg.bitruby.usersapp.client.veriff.api.model.NewSession;
import kg.bitruby.usersapp.client.veriff.api.model.NewSessionVerification;
import kg.bitruby.usersapp.client.veriff.api.model.NewSessionVerificationAddress;
import kg.bitruby.usersapp.client.veriff.api.model.Session;
import kg.bitruby.usersapp.common.AppContextHolder;
import kg.bitruby.usersapp.core.event.NewUserRegistrationEvent;
import kg.bitruby.usersapp.core.event.UserCompleteRegistrationEvent;
import kg.bitruby.usersapp.core.event.UserVerificationDecisionEvent;
import kg.bitruby.usersapp.outcomes.kafka.service.KafkaProducerService;
import kg.bitruby.usersapp.outcomes.postgres.domain.*;
import kg.bitruby.usersapp.outcomes.postgres.repository.OtpRegistrationTokenRepository;
import kg.bitruby.usersapp.outcomes.postgres.repository.UserRepository;
import kg.bitruby.usersapp.outcomes.postgres.repository.UsersVerificationSessionsRepository;
import kg.bitruby.usersapp.outcomes.rest.veriffClient.api.VeriffApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
    
    UserEntity userEntity = new UserEntity();
    userEntity.setPhone(registerUser.getPhone());
    userEntity.setEmail(registerUser.getEmail());
    userEntity.setPassword(passwordEncoder.encode(registerUser.getPassword()));
    userEntity.setRole(AuthorityRoleEnum.USER);
    userEntity.setEnabled(false);
    userEntity.setAccountNonLocked(false);
    userEntity.setCredentialsNonExpired(false);
    userEntity.setRegistrationComplete(false);
    userEntity.setVerified(false);
    userRepository.save(userEntity);
    publisher.publishEvent(new NewUserRegistrationEvent(userEntity));

    return new Base(true, OffsetDateTime.now());

  }

  @Transactional
  public Base completeRegistration(OtpCodeCheck otpCodeCheck) {
    checkToken(otpCodeCheck);
    UserEntity userEntity = getUserEntityByGrantType(otpCodeCheck.getGrantType(), otpCodeCheck.getSendTo());
    userEntity.setEnabled(true);
    userEntity.setVerified(false);
    userEntity.setAccountNonLocked(true);
    userEntity.setCredentialsNonExpired(true);
    userEntity.setRegistrationComplete(false);
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
        userEntity.setVerified(true);
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

    kg.bitruby.usersapp.client.veriff.api.model.Person person =
        new kg.bitruby.usersapp.client.veriff.api.model.Person();
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


}
