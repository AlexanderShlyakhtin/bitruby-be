package kg.bitruby.authservice.core;

import jakarta.transaction.Transactional;
import kg.bitruby.authservice.outcomes.postgres.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsByEmailService implements UserDetailsService {

  private final UsersService usersService;

  @Override
  public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity user = usersService.getUserInfoByEmail(email);
    CustomUserDetails customUserDetails = new CustomUserDetails(user);
    if(!customUserDetails.isEnabled()
        || !customUserDetails.isAccountNonLocked()
        || !customUserDetails.isCredentialsNonExpired()) {
      throw new UsernameNotFoundException("Error. User account not valid. Make sure that registration is completed, or account is enabled, non locked and have valid credential");
    }
    return customUserDetails;
  }
}
