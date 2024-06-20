package kg.bitruby.usersapp.core.users;

import kg.bitruby.usersapp.api.model.*;
import kg.bitruby.usersapp.core.event.NewUserRegistrationEvent;
import kg.bitruby.usersapp.core.event.UserCompleteRegistrationEvent;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersapp.outcomes.postgres.domain.AuthorityRoleEnum;
import kg.bitruby.usersapp.outcomes.postgres.domain.OtpRegistrationTokenEntity;
import kg.bitruby.usersapp.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersapp.outcomes.postgres.repository.OtpRegistrationTokenRepository;
import kg.bitruby.usersapp.outcomes.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UsersService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher publisher;
  private final OtpRegistrationTokenRepository otpTokenRepository;

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
    userEntity.setUserDataNonPending(false);
    userRepository.save(userEntity);
    publisher.publishEvent(new NewUserRegistrationEvent(userEntity));

    return new Base(true, OffsetDateTime.now());

  }

  @Transactional
  public Base completeRegistration(OtpCodeCheck otpCodeCheck) {
    checkToken(otpCodeCheck);
    UserEntity userEntity = getUserEntityByGrantType(otpCodeCheck.getGrantType(), otpCodeCheck.getSendTo());
    userEntity.setEnabled(true);
    userEntity.setUserDataNonPending(false);
    userEntity.setAccountNonLocked(true);
    userEntity.setCredentialsNonExpired(true);
    userEntity.setRegistrationComplete(false);
    userRepository.save(userEntity);
    publisher.publishEvent(new UserCompleteRegistrationEvent(userEntity));

    return new Base(true, OffsetDateTime.now());
  }


  public Base restorePassword(RestorePassword restorePassword) {
    checkToken(new OtpCodeCheck(restorePassword.getGrantType(), restorePassword.getSendTo(), restorePassword.getOtp()));
    UserEntity userEntity = getUserEntityByGrantType(restorePassword.getGrantType(), restorePassword.getSendTo());
    userEntity.setPassword(passwordEncoder.encode(restorePassword.getPassword()));
    userRepository.save(userEntity);
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
}
