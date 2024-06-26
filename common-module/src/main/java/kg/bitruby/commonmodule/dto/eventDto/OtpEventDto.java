package kg.bitruby.commonmodule.dto.eventDto;

import kg.bitruby.usersapp.api.model.GrantType;
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
