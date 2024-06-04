package com.bitruby.usersapp.outcomes.postgres.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "verification_token")
public class VerificationTokenEntity {

  //Expiration time 30 miutes
  private static final int EXPIRATION_TIME = 30;
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private UUID token;
  private String role;
  private Date expirationTime;
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "FK_USER_VERIFY_TOKEN"))
  private UserEntity user;
  private Boolean valid;

  public VerificationTokenEntity(UserEntity user, String role, UUID token, Boolean valid) {
    super();
    this.token = token;
    this.user = user;
    this.role = role;
    this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
    this.valid = valid;
  }

  private Date calculateExpirationDate(int expirationTime) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(new Date().getTime());
    calendar.add(Calendar.MINUTE, expirationTime);
    return new Date(calendar.getTime().getTime());
  }

}
