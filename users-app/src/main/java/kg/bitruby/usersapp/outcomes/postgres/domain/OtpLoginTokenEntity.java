package kg.bitruby.usersapp.outcomes.postgres.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "otp_token_login")
public class OtpLoginTokenEntity {

  private static final int EXPIRATION_TIME = 30;
  @Id
  private String id;
  private String token;
  private Date expirationTime;
  private Boolean valid;

  public OtpLoginTokenEntity(String id, String token, Boolean valid) {
    this.id = id;
    this.token = token;
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
