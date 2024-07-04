package kg.bitruby.usersservice.core.restorepassword;

import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersservice.api.model.Base;
import kg.bitruby.usersservice.api.model.GrantType;
import kg.bitruby.usersservice.api.model.OtpCodeCheck;
import kg.bitruby.usersservice.api.model.RestorePassword;
import kg.bitruby.usersservice.outcomes.postgres.domain.OtpRestorePasswordTokenEntity;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersservice.outcomes.postgres.repository.OtpRestorePasswordTokenEntityRepository;
import kg.bitruby.usersservice.outcomes.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RestorePasswordService {

  private final OtpRestorePasswordTokenEntityRepository restorePasswordRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public Base restorePassword(RestorePassword restorePassword) {
    UserEntity userEntity = getUserEntityByGrantType(restorePassword.getGrantType(), restorePassword.getSendTo());
    if (passwordEncoder.matches(restorePassword.getPassword(), userEntity.getPassword())) {
      throw new BitrubyRuntimeExpection("Новый пароль не должен совпадать со старым");
    }

    checkToken(new OtpCodeCheck(restorePassword.getGrantType(), restorePassword.getSendTo(), restorePassword.getOtp()));
    userEntity.setPassword(passwordEncoder.encode(restorePassword.getPassword()));
    userRepository.save(userEntity);
    return new Base(true, OffsetDateTime.now());
  }

  private void checkToken(OtpCodeCheck otpCodeCheck) {
    OtpRestorePasswordTokenEntity token =
        restorePasswordRepository.findById(otpCodeCheck.getSendTo()).orElseThrow(() -> new RuntimeException("Token not found"));
    if( Objects.equals(token.getToken(), otpCodeCheck.getOtp()) && !new Date().toInstant().isAfter(token.getExpirationTime().toInstant()) ) {
      restorePasswordRepository.deleteById(otpCodeCheck.getSendTo());
    } else throw new BitrubyRuntimeExpection("Token is not valid");
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
}
