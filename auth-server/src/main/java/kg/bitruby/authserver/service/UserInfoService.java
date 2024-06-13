package kg.bitruby.authserver.service;


import kg.bitruby.authserver.entity.UserEntity;
import kg.bitruby.authserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInfoService {

  private final UserRepository userInfoRepository;

  public UserEntity getUserInfo(String userName) {
    Optional<UserEntity> byEmail = userInfoRepository.findByEmail(userName);
    if(byEmail.isPresent()){
      return byEmail.get();
    }
    else throw new UsernameNotFoundException("User not found");
  }
}
