package kg.bitruby.commonmodule.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@MappedSuperclass
public class UserEntityBase implements Serializable {
  @Column(name = "phone", length = 100, nullable = false)
  private String phone;
  @Column(name = "email", nullable = false, unique = true)
  private String email;
  @Column(name = "password", nullable = false)
  private String password;
  @Column(name = "first_name")
  private String firstName;
  @Column(name = "last_name")
  private String lastName;
  @Column(name = "country")
  private String country;
  @Column(name = "address")
  private String address;
  @Column(name = "is_enabled", nullable = false)
  private boolean isEnabled = false;
  @Column(name = "is_email_confirmed", nullable = false)
  private boolean isEmailConfirmed = false;
  @Column(name = "is_phone_confirmed", nullable = false)
  private boolean isPhoneConfirmed = true;
  @Column(name = "account_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private AccountStatus accountStatus;
  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private AuthorityRoleEnum role;
}
