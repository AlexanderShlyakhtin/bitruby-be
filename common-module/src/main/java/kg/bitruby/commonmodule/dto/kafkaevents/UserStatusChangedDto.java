package kg.bitruby.commonmodule.dto.kafkaevents;

import kg.bitruby.commonmodule.domain.ChangeUserAccountEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusChangedDto {

  private UUID userId;
  private ChangeUserAccountEvent newAccountStatus;

}

