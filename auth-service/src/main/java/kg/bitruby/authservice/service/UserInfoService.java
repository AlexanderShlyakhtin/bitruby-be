package kg.bitruby.authservice.service;


import kg.bitruby.authservice.outcomes.postgres.entity.UserEntity;
import kg.bitruby.authservice.outcomes.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInfoService {

  private final UserRepository userInfoRepository;

  public UserEntity getUserInfoByEmail(String name) {
    Optional<UserEntity> byEmail = userInfoRepository.findByEmailAndIsEnabledTrue(name);
    if(byEmail.isPresent()){
      return byEmail.get();
    }
    else throw new UsernameNotFoundException("User not found");
  }

  public UserEntity getUserInfoByPhone(String name) {
    Optional<UserEntity> byEmail = userInfoRepository.findByPhoneAndIsEnabledTrue(name);
    if(byEmail.isPresent()){
      return byEmail.get();
    }
    else throw new UsernameNotFoundException("User not found");
  }
}
