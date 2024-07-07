package kg.bitruby.commonmodule.dto.kafkaevents;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationEventDto {

  private UUID id;
  private UUID attemptId;
  private String feature;
  private Integer code;
  private VerificationEventAction action;
  private String vendorData;
}
