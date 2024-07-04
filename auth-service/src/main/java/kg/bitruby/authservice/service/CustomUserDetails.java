package kg.bitruby.authservice.service;

import kg.bitruby.authservice.entity.UserEntity;
import kg.bitruby.commonmodule.domain.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

  private final UserEntity user;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(user.getRole().getValue()));
    return authorities;
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  public String getPhone() {
    return user.getPhone();
  }

  public UserEntity getUserEntity() {
    return user;
  }

  @Override
  public boolean isEnabled() {
    return user.isEnabled();
  }

  @Override
  public boolean isAccountNonLocked() {
    return !user.getAccountStatus().equals(AccountStatus.ACCOUNT_LOCK);
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return !user.getAccountStatus().equals(AccountStatus.CREDENTIAL_EXPIRED);
  }
}
