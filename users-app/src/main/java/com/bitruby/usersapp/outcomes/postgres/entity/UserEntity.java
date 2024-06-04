package com.bitruby.usersapp.outcomes.postgres.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
  @NotNull
  private String email;
  private String firstName;
  private String lastName;

  @NotNull
  private String password;
  @NotNull
  private boolean isEnabled = false;
  @NotNull
  private boolean isAccountNonLocked = false;
  @NotNull
  private boolean isCredentialsNonExpired = true;
  @NotNull
  private boolean isRegistrationComplete = false;

  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private AuthorityRoleEnum role;

}
