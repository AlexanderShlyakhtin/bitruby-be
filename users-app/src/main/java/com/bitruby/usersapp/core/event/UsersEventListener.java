package com.bitruby.usersapp.core.event;

import com.bitruby.usersapp.api.model.GrantType;
import com.bitruby.usersapp.api.model.OtpCode;
import com.bitruby.usersapp.core.otp.OtpService;
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
  public void userCompleteRegistrationEventEventListener(UserCompleteRegistrationEvent event) {
    log.info("User complete registration with email {}",
        event.getUser().getEmail());
  }

}
