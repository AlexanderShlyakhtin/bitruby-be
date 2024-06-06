package com.bitruby.usersapp.core.event;

import com.bitruby.usersapp.api.model.GrantType;
import com.bitruby.usersapp.api.model.OtpCode;
import com.bitruby.usersapp.core.otp.OtpService;
import com.bitruby.usersapp.core.users.UsersService;
import com.bitruby.usersapp.outcomes.postgres.entity.AuthorityRoleEnum;
import com.bitruby.usersapp.outcomes.postgres.entity.OtpTokenEntity;
import com.bitruby.usersapp.outcomes.postgres.repository.OtpTokenRepository;
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

  private final OtpService otpService;

  @EventListener(NewUserRegistrationEvent.class)
  public void newUserEventListener(NewUserRegistrationEvent event) {

    log.info("User created. Mail with link on password creation sent on {}",
        event.getUser().getEmail());
    OtpCode otpCode = new OtpCode();
    otpCode.setGrantType(GrantType.EMAIL_PASSWORD);
    otpCode.setSendTo(event.getUser().getEmail());
    otpService.generateOtpCodeForLogin(otpCode);
  }

  @EventListener(UserCompleteRegistrationEvent.class)
  public void userCompleteRegistrationEventEventListener(UserCompleteRegistrationEvent event) {
    log.info("User complete registration with email {}",
        event.getUser().getEmail());
  }

}
