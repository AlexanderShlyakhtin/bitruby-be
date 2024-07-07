package kg.bitruby.usersservice.core.users;

import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersservice.api.model.GrantType;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import kg.bitruby.usersservice.outcomes.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

  private List<UserEntity> findByEmailAndIsEnabledTrue(String sendTo) {
    return userRepository.findByEmailAndIsEnabledTrue(sendTo);
  }

  private List<UserEntity> findByPhoneAndIsEnabledTrue(String sendTo) {
    return userRepository.findByPhoneAndIsEnabledTrue(sendTo);
  }

}
