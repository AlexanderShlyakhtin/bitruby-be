package kg.bitruby.usersservice.core.services.users;

import kg.bitruby.commonmodule.domain.AccountStatus;
import kg.bitruby.commonmodule.dto.kafkaevents.UserStatusEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersservice.api.model.GrantType;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersservice.outcomes.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {
  private final UserRepository userRepository;

  public UserEntity findUserById(UUID userId) {
    return userRepository.findById(userId).orElseThrow(() -> new BitrubyRuntimeExpection(
        String.format("User with id: %s not found for the verification event", userId)));
  }

  public UserEntity save(UserEntity userEntity) {
    return userRepository.save(userEntity);
  }

  public UserEntity checkUserWithTokenAndReturn(GrantType grantType, String sendTo) {
    List<UserEntity> userEntityList = new ArrayList<>();
    if(grantType.equals(GrantType.EMAIL_PASSWORD)) {
      userEntityList = findByEmailAndIsEnabledTrue(sendTo);
      if(userEntityList.size() != 1) throw new BitrubyRuntimeExpection("User not exists");
    } else if (grantType.equals(GrantType.PHONE_PASSWORD)) {
      userEntityList = findByPhoneAndIsEnabledTrue(sendTo);
      if(userEntityList.size() != 1) throw new BitrubyRuntimeExpection("User not exists");
    } else throw new BitrubyRuntimeExpection("Unknown Grant type");
    return userEntityList.stream().findFirst().get();
  }

  @Transactional(transactionManager = "transactionManager")
  public void handleChangeUserAccountEvents(UserStatusEventDto event) {
    UserEntity userEntity = findUserById(event.getUserId());
    switch (event.getNewAccountStatus()) {
      case BYBIT_ACCOUNT_CREATED -> {
        if( userEntity.isEnabled() &&
            !userEntity.getAccountStatus().equals(AccountStatus.ACCOUNT_LOCK) &&
            userEntity.getAccountStatus().equals(AccountStatus.BYBIT_ACCOUNT_NOT_CREATED)
        ) {
          userEntity.setAccountStatus(AccountStatus.OK);
          save(userEntity);
        }
      }
      case DELETE_ACCOUNT -> {
        userEntity.setEnabled(false);
        save(userEntity);
      }
      case ACCOUNT_VERIFIED -> {
        if( userEntity.isEnabled() &&
            !userEntity.getAccountStatus().equals(AccountStatus.ACCOUNT_LOCK) &&
            userEntity.getAccountStatus().equals(AccountStatus.NOT_VERIFIED)
        ) {
          userEntity.setAccountStatus(AccountStatus.BYBIT_ACCOUNT_NOT_CREATED);
          save(userEntity);
        }
      }
      case CREDENTIAL_EXPIRED -> {
        if( userEntity.isEnabled() &&
            !userEntity.getAccountStatus().equals(AccountStatus.ACCOUNT_LOCK)
        ) {
          userEntity.setAccountStatus(AccountStatus.CREDENTIAL_EXPIRED);
          save(userEntity);
        }
      }
      case LOCK_ACCOUNT -> {
        if(
            userEntity.isEnabled()
        ) {
          userEntity.setAccountStatus(AccountStatus.ACCOUNT_LOCK);
          save(userEntity);
        }
      }
    }

  }

  private List<UserEntity> findByEmailAndIsEnabledTrue(String sendTo) {
    return userRepository.findByEmailAndIsEnabledTrue(sendTo);
  }

  private List<UserEntity> findByPhoneAndIsEnabledTrue(String sendTo) {
    return userRepository.findByPhoneAndIsEnabledTrue(sendTo);
  }


}
