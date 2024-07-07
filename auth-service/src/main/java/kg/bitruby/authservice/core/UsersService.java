package kg.bitruby.authservice.core;


import kg.bitruby.authservice.api.model.GrantType;
import kg.bitruby.authservice.outcomes.postgres.entity.UserEntity;
import kg.bitruby.authservice.outcomes.postgres.repository.UsersRepository;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersService {

  private final UsersRepository usersRepository;

  public UserEntity getUserInfoByEmail(String email) {
    List<UserEntity> byEmail = usersRepository.findByEmailAndIsEnabledTrue(email);
    if(byEmail.size() == 1 && byEmail.get(0).isEmailConfirmed() ){
      return byEmail.stream().findFirst().get();
    }
    else throw new UsernameNotFoundException("User not found");
  }

  public UserEntity getUserInfoByPhone(String phone) {
    List<UserEntity> byEmail = usersRepository.findByPhoneAndIsEnabledTrue(phone);
    if(byEmail.size() == 1 && byEmail.get(0).isPhoneConfirmed() ){
      return byEmail.stream().findFirst().get();
    }
    else throw new UsernameNotFoundException("User not found");
  }

  public UserEntity checkUserWithTokenAndReturn(GrantType grantType, String sendTo) {
    UserEntity userEntity;
    if(grantType.equals(GrantType.EMAIL_PASSWORD)) {
      userEntity = getUserInfoByEmail(sendTo);
    } else if (grantType.equals(GrantType.PHONE_PASSWORD)) {
      userEntity = getUserInfoByPhone(sendTo);
    } else throw new BitrubyRuntimeExpection("Unknown Grant type");
    return userEntity;
  }

}
