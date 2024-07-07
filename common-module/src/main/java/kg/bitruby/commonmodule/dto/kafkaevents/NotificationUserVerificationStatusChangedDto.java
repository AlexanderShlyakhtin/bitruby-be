package kg.bitruby.commonmodule.dto.kafkaevents;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationUserVerificationStatusChangedDto {

  private UUID userId;
  private String email;
  private VerificationDecisionStatus status;

}
