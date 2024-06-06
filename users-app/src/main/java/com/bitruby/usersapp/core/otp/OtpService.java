package com.bitruby.usersapp.core.otp;

import com.bitruby.usersapp.api.model.OtpCode;
import com.bitruby.usersapp.outcomes.postgres.entity.OtpTokenEntity;
import com.bitruby.usersapp.outcomes.postgres.repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

  private final OtpTokenRepository otpTokenRepository;

  private static final String CHARACTERS = "0123456789";
  private static final Random RANDOM = new SecureRandom();

  public void generateOtpCodeForLogin(OtpCode otpCode) {
    StringBuilder sb = new StringBuilder(6);
    for (int i = 0; i < 6; i++) {
      int randomIndex = RANDOM.nextInt(CHARACTERS.length());
      sb.append(CHARACTERS.charAt(randomIndex));
    }
    OtpTokenEntity save =
        otpTokenRepository.save(new OtpTokenEntity(otpCode.getSendTo(), sb.toString(), true));
    log.info("Token {} saved for {}", save.getToken(), save.getToken());
  }
}
