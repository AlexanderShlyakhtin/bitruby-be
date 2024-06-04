package com.bitruby.usersapp.core.event;

import com.bitruby.usersapp.outcomes.postgres.entity.AuthorityRoleEnum;
import com.bitruby.usersapp.outcomes.postgres.entity.VerificationTokenEntity;
import com.bitruby.usersapp.outcomes.postgres.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class UsersEventListener {

  @Value("${bitruby.users-app.complete-registration-url}")
  private String registrationLink;

  private final VerificationTokenRepository tokenRepository;

  @EventListener({NewUserRegistrationEvent.class})
  public void newUserEventListener(NewUserRegistrationEvent event) {

    UUID token = UUID.randomUUID();
    String formattedLink = String.format("%s/%s", registrationLink, token);

    //sendVerificationEmail()

    log.info("User created. Mail with link on password creation sent on {}. Link: {}",
        event.getUser().getEmail(), formattedLink);
    tokenRepository.save(new VerificationTokenEntity(event.getUser(), AuthorityRoleEnum.USER.getValue(), token, true));
  }

  @EventListener({UserCompleteRegistrationEvent.class})
  public void userCompleteRegistrationEventEventListener(NewUserRegistrationEvent event) {

    log.info("User complete registration with email {}",
        event.getUser().getEmail());
  }

}
