package com.bitruby.usersapp.core.otp;

import com.bitruby.usersapp.api.model.GrantType;
import com.bitruby.usersapp.api.model.OtpCode;
import com.bitruby.usersapp.api.model.OtpCodeLogin;
import com.bitruby.usersapp.exceptions.BitrubyRuntimeExpection;
import com.bitruby.usersapp.outcomes.postgres.entity.OtpLoginTokenEntity;
import com.bitruby.usersapp.outcomes.postgres.entity.OtpRegistrationTokenEntity;
import com.bitruby.usersapp.outcomes.postgres.entity.UserEntity;
import com.bitruby.usersapp.outcomes.postgres.repository.OtpRegistrationTokenEntityRepository;
import com.bitruby.usersapp.outcomes.postgres.repository.OtpTokenRepository;
import com.bitruby.usersapp.outcomes.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
  private final OtpRegistrationTokenEntityRepository otpRegistrationTokenEntityRepository;
  private final UserRepository userRepository;

  private final OtpTokenRepository otpTokenRepository;
  private final PasswordEncoder passwordEncoder;

  private static final String CHARACTERS = "0123456789";
  private static final Random RANDOM = new SecureRandom();

  public void generateOtpCodeForLogin(OtpCodeLogin otpCode) {
    UserEntity userEntity;
    if(otpCode.getGrantType().equals(GrantType.EMAIL_PASSWORD)) {
      userEntity = userRepository.findByEmail(otpCode.getSendTo())
          .orElseThrow(() -> new BitrubyRuntimeExpection("User not register"));
    } else if (otpCode.getGrantType().equals(GrantType.PHONE_PASSWORD)) {
      userEntity = userRepository.findByPhone(otpCode.getSendTo())
          .orElseThrow(() -> new BitrubyRuntimeExpection("User not register"));
    } else {
      throw new BitrubyRuntimeExpection("Unknown Grant Type");
    }
    if(!passwordEncoder.matches(otpCode.getPassword(), userEntity.getPassword())) {
      throw new BitrubyRuntimeExpection("Invalid credentials");
    }
    OtpLoginTokenEntity save =
        otpTokenRepository.save(new OtpLoginTokenEntity(otpCode.getSendTo(), generateRandomCode(), true));
    log.info("Token {} saved for {}", save.getToken(), otpCode.getSendTo());
  }



  public void generateOtpCodeForRegistration(OtpCode otpCode) {
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
