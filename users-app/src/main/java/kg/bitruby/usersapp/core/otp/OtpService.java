package kg.bitruby.usersapp.core.otp;

import kg.bitruby.usersapp.api.model.Base;
import kg.bitruby.usersapp.api.model.GrantType;
import kg.bitruby.usersapp.api.model.OtpCode;
import kg.bitruby.usersapp.api.model.OtpCodeLogin;
import kg.bitruby.usersapp.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersapp.outcomes.postgres.domain.OtpLoginTokenEntity;
import kg.bitruby.usersapp.outcomes.postgres.domain.OtpRegistrationTokenEntity;
import kg.bitruby.usersapp.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersapp.outcomes.postgres.repository.OtpRegistrationTokenRepository;
import kg.bitruby.usersapp.outcomes.postgres.repository.OtpLoginTokenRepository;
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
@Transactional
public class OtpService {
  private final OtpRegistrationTokenRepository otpRegistrationTokenEntityRepository;
  private final UserRepository userRepository;

  private final OtpLoginTokenRepository otpTokenRepository;
  private final PasswordEncoder passwordEncoder;

  private static final String CHARACTERS = "0123456789";
  private static final Random RANDOM = new SecureRandom();

  public Base generateOtpCodeForLogin(OtpCodeLogin otpCode) {
    UserEntity userEntity;
    if(otpCode.getGrantType().equals(GrantType.EMAIL_PASSWORD)) {
      userEntity = userRepository.findByEmail(otpCode.getSendTo())
          .orElseThrow(() -> new BitrubyRuntimeExpection("Неверная пара логин/пароль"));
    } else if (otpCode.getGrantType().equals(GrantType.PHONE_PASSWORD)) {
      userEntity = userRepository.findByPhone(otpCode.getSendTo())
          .orElseThrow(() -> new BitrubyRuntimeExpection("Неверная пара логин/пароль"));
    } else {
      throw new BitrubyRuntimeExpection("Unknown Grant Type");
    }
    if(!passwordEncoder.matches(otpCode.getPassword(), userEntity.getPassword())) {
      throw new BitrubyRuntimeExpection("Неверная пара логин/пароль");
    }
    OtpLoginTokenEntity save =
        otpTokenRepository.save(new OtpLoginTokenEntity(otpCode.getSendTo(), generateRandomCode(), true));
    log.info("Token {} saved for {}", save.getToken(), otpCode.getSendTo());
    return new Base(true, OffsetDateTime.now());
  }



  public Base generateOtpCodeForRegistration(OtpCode otpCode) {
    if(otpCode.getGrantType().equals(GrantType.EMAIL_PASSWORD)) {
      userRepository.findByEmail(otpCode.getSendTo()).orElseThrow(() -> {
        throw new BitrubyRuntimeExpection("User not exists");
      });
    } else if (otpCode.getGrantType().equals(GrantType.PHONE_PASSWORD)) {
      userRepository.findByPhone(otpCode.getSendTo()).orElseThrow(() -> {
        throw new BitrubyRuntimeExpection("User not exists");
      });
    }
    OtpRegistrationTokenEntity save = otpRegistrationTokenEntityRepository.save(
        new OtpRegistrationTokenEntity(otpCode.getSendTo(), generateRandomCode(), true));
    log.info("Token for Registration {} saved for {}", save.getToken(), otpCode.getSendTo());
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
}
