package com.bitruby.usersapp.core.users;

import com.bitruby.usersapp.api.model.GrantType;
import com.bitruby.usersapp.api.model.OtpCodeCheck;
import com.bitruby.usersapp.core.event.NewUserRegistrationEvent;
import com.bitruby.usersapp.core.event.UserCompleteRegistrationEvent;
import com.bitruby.usersapp.exceptions.BitrubyRuntimeExpection;
import com.bitruby.usersapp.outcomes.postgres.entity.AuthorityRoleEnum;
import com.bitruby.usersapp.outcomes.postgres.entity.UserEntity;
import com.bitruby.usersapp.outcomes.postgres.entity.OtpTokenEntity;
import com.bitruby.usersapp.outcomes.postgres.repository.UserRepository;
import com.bitruby.usersapp.outcomes.postgres.repository.OtpTokenRepository;
import com.bitruby.usersapp.api.model.RegisterUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UsersService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher publisher;
  private final OtpTokenRepository otpTokenRepository;

  @Transactional
  public void registerUser(RegisterUser registerUser) {
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail(registerUser.getEmail());
    userEntity.setPassword(passwordEncoder.encode(registerUser.getPassword()));
    userEntity.setRole(AuthorityRoleEnum.USER);
    userEntity.setEnabled(false);
    userEntity.setAccountNonLocked(false);
    userEntity.setCredentialsNonExpired(false);
    userEntity.setRegistrationComplete(false);
    userRepository.save(userEntity);
    publisher.publishEvent(new NewUserRegistrationEvent(userEntity));
  }

  @Transactional
  public void completeRegistration(OtpCodeCheck otpCodeCheck) {
    checkToken(otpCodeCheck);
    if(otpCodeCheck.getGrantType().equals(GrantType.EMAIL_PASSWORD)) {
      UserEntity userEntity = userRepository.findByEmail(otpCodeCheck.getSendTo())
          .orElseThrow(() -> new BitrubyRuntimeExpection("Token is not valid"));
      userEntity.setEnabled(true);
      userEntity.setAccountNonLocked(true);
      userEntity.setCredentialsNonExpired(true);
      userEntity.setRegistrationComplete(true);
      userRepository.save(userEntity);
      publisher.publishEvent(new UserCompleteRegistrationEvent(userEntity));
    } else if(otpCodeCheck.getGrantType().equals(GrantType.PHONE_PASSWORD)) {
      UserEntity userEntity = userRepository.findByPhone(otpCodeCheck.getSendTo())
          .orElseThrow(() -> new BitrubyRuntimeExpection("Token is not valid"));
      userEntity.setEnabled(true);
      userEntity.setAccountNonLocked(true);
      userEntity.setCredentialsNonExpired(true);
      userEntity.setRegistrationComplete(true);
      userRepository.save(userEntity);
      publisher.publishEvent(new UserCompleteRegistrationEvent(userEntity));
    } else throw new BitrubyRuntimeExpection("Registration not complete");
  }

  private void checkToken(OtpCodeCheck otpCodeCheck) {
    OtpTokenEntity token =
        otpTokenRepository.findById(otpCodeCheck.getSendTo()).orElseThrow(() -> new RuntimeException("Token not found"));
    if( Objects.equals(token.getToken(), otpCodeCheck.getOtp()) && !new Date().toInstant().isAfter(token.getExpirationTime().toInstant()) ) {
      otpTokenRepository.deleteById(otpCodeCheck.getSendTo());
    } else throw new BitrubyRuntimeExpection("Token is not valid");
  }
}
