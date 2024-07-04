package kg.bitruby.usersservice.outcomes.redis.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@RedisHash(value = "OtpLogin", timeToLive = 10000L )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtpRegistration implements Serializable {

  @Id
  private UUID id;
  private UUID userId;
  private String token;
  private Date expirationTime;
}
