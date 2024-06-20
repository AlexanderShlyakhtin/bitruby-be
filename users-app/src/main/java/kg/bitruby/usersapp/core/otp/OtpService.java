package kg.bitruby.usersapp.core.otp;

import kg.bitruby.commonmodule.dto.eventDto.OtpEventDto;
import kg.bitruby.usersapp.api.model.Base;
import kg.bitruby.usersapp.api.model.GrantType;
import kg.bitruby.usersapp.api.model.OtpCode;
import kg.bitruby.usersapp.api.model.OtpCodeLogin;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersapp.outcomes.kafka.service.KafkaProducerService;
import kg.bitruby.usersapp.outcomes.postgres.domain.OtpLoginTokenEntity;
import kg.bitruby.usersapp.outcomes.postgres.domain.OtpRegistrationTokenEntity;
import kg.bitruby.usersapp.outcomes.postgres.domain.OtpRestorePasswordTokenEntity;
import kg.bitruby.usersapp.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersapp.outcomes.postgres.repository.OtpRegistrationTokenRepository;
import kg.bitruby.usersapp.outcomes.postgres.repository.OtpLoginTokenRepository;
import kg.bitruby.usersapp.outcomes.postgres.repository.OtpRestorePasswordTokenEntityRepository;
import kg.bitruby.usersapp.outcomes.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
  private final OtpRestorePasswordTokenEntityRepository otpRestorePasswordTokenEntityRepository;

  private final OtpRegistrationTokenRepository otpRegistrationTokenEntityRepository;
  private final UserRepository userRepository;
  private final OtpLoginTokenRepository otpTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final KafkaProducerService kafkaProducerService;

  private static final String CHARACTERS = "0123456789";
  private static final Random RANDOM = new SecureRandom();

  @Transactional(transactionManager = "transactionManager")
  public Base generateOtpCodeForLogin(OtpCodeLogin otpCode) {
    UserEntity userEntity;
    String sendTo = otpCode.getSendTo();
    if(otpCode.getGrantType().equals(GrantType.EMAIL_PASSWORD)) {
      userEntity = userRepository.findByEmail(sendTo)
          .orElseThrow(() -> new BitrubyRuntimeExpection("Неверная пара логин/пароль"));
    } else if (otpCode.getGrantType().equals(GrantType.PHONE_PASSWORD)) {
      userEntity = userRepository.findByPhone(sendTo)
          .orElseThrow(() -> new BitrubyRuntimeExpection("Неверная пара логин/пароль"));
    } else {
      throw new BitrubyRuntimeExpection("Unknown Grant Type");
    }
    if(!passwordEncoder.matches(otpCode.getPassword(), userEntity.getPassword())) {
      throw new BitrubyRuntimeExpection("Неверная пара логин/пароль");
    }
    String code = generateRandomCode();
    kafkaProducerService.emitOtpLoginEventMessage(OtpEventDto.builder().code(code).sendTo(sendTo).grantType(otpCode.getGrantType()).build());
    otpTokenRepository.save(new OtpLoginTokenEntity(sendTo, code, true));
    return new Base(true, OffsetDateTime.now());
  }

  @Transactional(transactionManager = "transactionManager")
  public Base generateOtpCodeForRegistration(OtpCode otpCode) {
    String sendTo = otpCode.getSendTo();
    if(otpCode.getGrantType().equals(GrantType.EMAIL_PASSWORD)) {
      userRepository.findByEmail(sendTo).orElseThrow(() -> new BitrubyRuntimeExpection(
          "User not exists"));
    } else if (otpCode.getGrantType().equals(GrantType.PHONE_PASSWORD)) {
      userRepository.findByPhone(sendTo).orElseThrow(() -> new BitrubyRuntimeExpection(
          "User not exists"));
    }
    String code = generateRandomCode();
    kafkaProducerService.emitOtpRegistrationEventMessage(OtpEventDto.builder().code(code).sendTo(sendTo).grantType(otpCode.getGrantType()).build());
    otpRegistrationTokenEntityRepository.save(new OtpRegistrationTokenEntity(sendTo, code, true));
    return new Base(true, OffsetDateTime.now());
  }


  private String generateRandomCode() {
    StringBuilder sb = new StringBuilder(6);
    for (int i = 0; i < 6; i++) {
      int randomIndex = RANDOM.nextInt(CHARACTERS.length());
      sb.append(CHARACTERS.charAt(randomIndex));
    }
    return sb.toString();
  }

  @Transactional(transactionManager = "transactionManager")
  public Base generateOtpCodeForRestoringPassword(OtpCode otpCode) {
    String sendTo = otpCode.getSendTo();
    if(otpCode.getGrantType().equals(GrantType.EMAIL_PASSWORD)) {
      userRepository.findByEmail(sendTo).orElseThrow(() -> new BitrubyRuntimeExpection(
          "User not exists"));
    } else if (otpCode.getGrantType().equals(GrantType.PHONE_PASSWORD)) {
      userRepository.findByPhone(sendTo).orElseThrow(() -> new BitrubyRuntimeExpection(
          "User not exists"));
    }
    String code = generateRandomCode();
    kafkaProducerService.emitOtpRestorePasswordEventMessage(OtpEventDto.builder().code(code).sendTo(sendTo).grantType(otpCode.getGrantType()).build());
    otpRestorePasswordTokenEntityRepository.save(new OtpRestorePasswordTokenEntity(sendTo, code, true));
    return new Base(true, OffsetDateTime.now());
  }
}
