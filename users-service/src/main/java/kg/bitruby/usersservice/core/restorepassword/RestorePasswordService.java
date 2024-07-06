package kg.bitruby.usersservice.core.restorepassword;

import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersservice.api.model.Base;
import kg.bitruby.usersservice.api.model.RestorePassword;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersservice.outcomes.postgres.repository.UserRepository;
import kg.bitruby.usersservice.outcomes.redis.domain.OtpRestorePassword;
import kg.bitruby.usersservice.outcomes.redis.repository.OtpRestorePasswordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

import static kg.bitruby.commonmodule.constants.AppConstants.TOKEN_NOT_VALID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestorePasswordService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final OtpRestorePasswordRepository otpRestorePasswordRepository;

  public Base restorePassword(RestorePassword restorePassword) {
    OtpRestorePassword otpRestorePassword = otpRestorePasswordRepository.findById(restorePassword.getRestorePasswordId().toString()).orElseThrow(() -> new BitrubyRuntimeExpection(TOKEN_NOT_VALID));
    UserEntity userEntity = findUserById(otpRestorePassword.getUserId());

    checkUserWithTokenForRestorePassword(userEntity);
    if (passwordEncoder.matches(restorePassword.getPassword(), userEntity.getPassword())) {
      throw new BitrubyRuntimeExpection("Новый пароль не должен совпадать со старым");
    }
    userEntity.setPassword(passwordEncoder.encode(restorePassword.getPassword()));
    userRepository.save(userEntity);
    return new Base(true, OffsetDateTime.now());
  }

  private UserEntity findUserById(UUID userId) {
    return userRepository.findById(userId).orElseThrow(() -> new BitrubyRuntimeExpection(
        String.format("User with id: %s not found for the verification event", userId)));
  }

  private void checkUserWithTokenForRestorePassword(UserEntity userEntity) {

    if (!userEntity.isEnabled()) {
      log.error("Попытка восстановить пароль для удаленного пользователя");
      throw new BitrubyRuntimeExpection("Пользователь не существует");
    }
    switch (userEntity.getAccountStatus()) {
      case ACCOUNT_LOCK -> {
        log.error("Попытка восстановить пароль для заблокированного пользователя");
        throw new BitrubyRuntimeExpection("Пользователь заблокирован");
      }
      case REGISTRATION_STAGE -> {
        log.error("Попытка восстановить пароль для не зарегистрированного пользователя");
        throw new BitrubyRuntimeExpection("Пользователь не существует");
      }
    }
  }

}
