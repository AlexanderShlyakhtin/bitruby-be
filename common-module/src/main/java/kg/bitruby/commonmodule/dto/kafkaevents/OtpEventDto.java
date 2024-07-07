package kg.bitruby.commonmodule.dto.kafkaevents;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpEventDto {
  private CommunicationType communicationType;
  private String code;
  private String sendTo;
}
