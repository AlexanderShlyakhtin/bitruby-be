package kg.bitruby.usersservice.core.event;

import kg.bitruby.commonmodule.dto.events.VerificationDecisionStatus;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserVerificationDecisionEvent extends ApplicationEvent {

  private UserEntity user;
  private VerificationDecisionStatus status;

  public UserVerificationDecisionEvent(UserEntity user, VerificationDecisionStatus status) {
    super(user);
    this.user = user;
    this.status = status;
  }


}
