package kg.bitruby.commonmodule.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class PreUserRegistrationBase extends UserEntityBase {
  @Id
  private String uuid;
}
