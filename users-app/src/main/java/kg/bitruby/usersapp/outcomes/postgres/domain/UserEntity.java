package kg.bitruby.usersapp.outcomes.postgres.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users", schema = "users")
public class UserEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "user_id")
  private UUID id;
  @Column(name = "phone", length = 100, nullable = false, unique = true)
  private String phone;
  @Column(name = "password", nullable = false)
  private String password;
  @Column(name = "email", nullable = false, unique = true)
  private String email;
  @Column(name = "first_name")
  private String firstName;
  @Column(name = "last_name")
  private String lastName;
  @Column(name = "address")
  private String address;
  @Column(name = "is_enabled", nullable = false)
  private boolean isEnabled = false;
  @Column(name = "is_account_non_locked", nullable = false)
  private boolean isAccountNonLocked = false;
  @Column(name = "is_credentials_non_expired", nullable = false)
  private boolean isCredentialsNonExpired = true;
  @Column(name = "is_registration_complete", nullable = false)
  private boolean isRegistrationComplete = false;
  @Column(name = "is_verified", nullable = false)
  private boolean isVerified = false;
  @Column(name = "is_bybit_account_created", nullable = false)
  private boolean isBybitAccountCreated = false;
  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private AuthorityRoleEnum role;

}
