package kg.bitruby.commonmodule.dto.events;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationDecisionDto {

  private UUID id;
  private UUID userId;
  private VerificationDecisionStatus status;

}
