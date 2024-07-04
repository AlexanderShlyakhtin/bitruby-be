package kg.bitruby.usersservice.core.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UsersEventListener {

  @EventListener(NewUserRegistrationEvent.class)
  public void newUserEventListener(NewUserRegistrationEvent event) {

    log.info("User created. Mail with link on password creation sent on {}",
        event.getUser().getEmail());
  }

  @EventListener(UserCompleteRegistrationEvent.class)
  public void userCompleteRegistrationEventListener(UserCompleteRegistrationEvent event) {
    log.info("User complete registration with email {}",
        event.getUser().getEmail());
  }

  @EventListener(UserVerificationDecisionEvent.class)
  public void userVerifiedEventListener(UserVerificationDecisionEvent event) {
    log.info("User verified event {}",
        event.getUser().getEmail());
  }

}
