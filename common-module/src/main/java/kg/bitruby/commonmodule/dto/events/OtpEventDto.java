package kg.bitruby.commonmodule.dto.events;

import kg.bitruby.usersservice.api.model.GrantType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpEventDto {
  private GrantType grantType;
  private String code;
  private String sendTo;
}
