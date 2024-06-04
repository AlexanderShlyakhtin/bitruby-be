package com.bitruby.authserver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users", schema = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "user_id")
  private UUID id;
  private String email;
  private String phone;
  private String firstName;
  private String lastName;
  private String password;
  private boolean isEnabled = false;
  private boolean isAccountNonLocked = false;
  private boolean isCredentialsNonExpired = true;
  private boolean isRegistrationComplete = false;

  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private AuthorityRoleEnum role;

}
