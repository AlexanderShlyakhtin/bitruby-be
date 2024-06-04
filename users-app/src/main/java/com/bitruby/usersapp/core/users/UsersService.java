package com.bitruby.usersapp.core.users;

import com.bitruby.usersapp.core.event.NewUserRegistrationEvent;
import com.bitruby.usersapp.exceptions.BitrubyNotFoundException;
import com.bitruby.usersapp.outcomes.postgres.entity.AuthorityRoleEnum;
import com.bitruby.usersapp.outcomes.postgres.entity.UserEntity;
import com.bitruby.usersapp.outcomes.postgres.entity.VerificationTokenEntity;
import com.bitruby.usersapp.outcomes.postgres.repository.UserRepository;
import com.bitruby.usersapp.outcomes.postgres.repository.VerificationTokenRepository;
import com.bitruby.usersapp.api.model.RegisterUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher publisher;
  private final VerificationTokenRepository verificationTokenRepository;

  @Transactional
  public void registerUser(RegisterUser registerUser) {
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail(registerUser.getEmail());
    userEntity.setPassword(passwordEncoder.encode(registerUser.getPassword()));
    userEntity.setRole(AuthorityRoleEnum.USER);
    userRepository.save(userEntity);
    publisher.publishEvent(new NewUserRegistrationEvent(userEntity));
  }

  @Transactional
  public void completeRegistration(UUID id) {
    VerificationTokenEntity verificationToken = verificationTokenRepository.findByTokenAndValid(id, true)
        .orElseThrow(() -> new BitrubyNotFoundException("Verification token not found"));
    if(checkToken(verificationToken)) {
      UserEntity user = verificationToken.getUser();
      user.setEnabled(true);
      user.setCredentialsNonExpired(true);
      user.setRegistrationComplete(true);
      user.setAccountNonLocked(true);
      userRepository.save(user);

      verificationToken.setValid(false);
      verificationTokenRepository.save(verificationToken);
      publisher.publishEvent(new NewUserRegistrationEvent(user));
    } else {
      throw new BitrubyNotFoundException("Token expired");
    }

  }

  private Boolean checkToken(VerificationTokenEntity verificationTokenEntity) {
    Date expirationTime = verificationTokenEntity.getExpirationTime();
    Date now = new Date();
    return !now.after(expirationTime);
  }
}
