package kg.bitruby.usersapp.core.users;

import kg.bitruby.usersapp.api.model.GrantType;
import kg.bitruby.usersapp.api.model.NewUser;
import kg.bitruby.usersapp.api.model.OtpCodeCheck;
import kg.bitruby.usersapp.core.event.NewUserRegistrationEvent;
import kg.bitruby.usersapp.core.event.UserCompleteRegistrationEvent;
import kg.bitruby.usersapp.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersapp.outcomes.postgres.domain.AuthorityRoleEnum;
import kg.bitruby.usersapp.outcomes.postgres.domain.OtpLoginTokenEntity;
import kg.bitruby.usersapp.outcomes.postgres.domain.OtpRegistrationTokenEntity;
import kg.bitruby.usersapp.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersapp.outcomes.postgres.repository.OtpLoginTokenRepository;
import kg.bitruby.usersapp.outcomes.postgres.repository.OtpRegistrationTokenRepository;
import kg.bitruby.usersapp.outcomes.postgres.repository.UserRepository;
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
  private final OtpRegistrationTokenRepository otpTokenRepository;

  @Transactional
  public void registerUser(NewUser registerUser) {
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
      userEntity.setUserDataNonPending(true);
      userEntity.setAccountNonLocked(true);
      userEntity.setCredentialsNonExpired(true);
      userEntity.setRegistrationComplete(false);
      userRepository.save(userEntity);
      publisher.publishEvent(new UserCompleteRegistrationEvent(userEntity));
    } else if(otpCodeCheck.getGrantType().equals(GrantType.PHONE_PASSWORD)) {
      UserEntity userEntity = userRepository.findByPhone(otpCodeCheck.getSendTo())
          .orElseThrow(() -> new BitrubyRuntimeExpection("Token is not valid"));
      userEntity.setEnabled(true);
      userEntity.setUserDataNonPending(true);
      userEntity.setAccountNonLocked(true);
      userEntity.setCredentialsNonExpired(true);
      userEntity.setRegistrationComplete(false);
      userRepository.save(userEntity);
      publisher.publishEvent(new UserCompleteRegistrationEvent(userEntity));
    } else throw new BitrubyRuntimeExpection("Registration not complete");
  }

  private void checkToken(OtpCodeCheck otpCodeCheck) {
    OtpRegistrationTokenEntity token =
        otpTokenRepository.findById(otpCodeCheck.getSendTo()).orElseThrow(() -> new RuntimeException("Token not found"));
    if( Objects.equals(token.getToken(), otpCodeCheck.getOtp()) && !new Date().toInstant().isAfter(token.getExpirationTime().toInstant()) ) {
      otpTokenRepository.deleteById(otpCodeCheck.getSendTo());
    } else throw new BitrubyRuntimeExpection("Token is not valid");
  }
}
