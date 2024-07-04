package kg.bitruby.usersservice.core.event;

import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserCompleteRegistrationEvent extends ApplicationEvent {

  private UserEntity user;
  public UserCompleteRegistrationEvent(UserEntity user) {
    super(user);
    this.user = user;
  }
}
