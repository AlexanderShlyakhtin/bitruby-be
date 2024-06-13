package kg.bitruby.authserver.service;

import kg.bitruby.authserver.entity.UserEntity;
import kg.bitruby.authserver.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsByEmailService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<UserEntity> user = userRepository.findByEmail(email);
    CustomUserDetails customUserDetails = user.map(CustomUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    if(!customUserDetails.isEnabled()
        || !customUserDetails.isAccountNonLocked()
        || !customUserDetails.isCredentialsNonExpired()) {
      throw new UsernameNotFoundException("Error. User account not valid. Make sure that registration is completed, or account is enabled, non locked and have valid credential");
    }
    return customUserDetails;
  }
}
