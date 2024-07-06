package kg.bitruby.commonmodule.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenBase implements Serializable {

  @Id
  private String id;
  private UUID userId;
  private String token;
  private Date expirationTime;
}
