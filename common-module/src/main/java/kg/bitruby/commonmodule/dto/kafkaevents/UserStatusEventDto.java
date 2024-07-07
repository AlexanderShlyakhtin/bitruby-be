package kg.bitruby.commonmodule.dto.kafkaevents;

import kg.bitruby.commonmodule.domain.EventType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusEventDto {

  private UUID userId;
  private EventType newAccountStatus;

}

