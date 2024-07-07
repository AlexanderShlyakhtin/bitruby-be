package kg.bitruby.commonmodule.dto.kafkaevents;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubAccountDto {
  private UUID userId;
}
