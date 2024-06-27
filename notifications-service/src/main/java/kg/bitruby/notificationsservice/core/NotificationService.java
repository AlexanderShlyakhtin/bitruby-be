package kg.bitruby.notificationsservice.core;

import kg.bitruby.commonmodule.dto.eventDto.OtpEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.notificationsservice.outcomes.email.EmailServiceClient;
import kg.bitruby.notificationsservice.outcomes.email.mapper.OtpEventEmailMapper;
import kg.bitruby.notificationsservice.outcomes.sms.SmsServiceClient;
import kg.bitruby.usersapp.api.model.GrantType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final EmailServiceClient emailServiceClient;
  private final SmsServiceClient smsServiceClient;
  private final OtpEventEmailMapper otpEventEmailMapper;
  public void sendNotificationRegistrationEvent(OtpEventDto otpEventDto) {
    if(otpEventDto.getGrantType().equals(GrantType.EMAIL_PASSWORD)) {
      emailServiceClient.sendEmail(otpEventEmailMapper.map(otpEventDto));
      log.info("Token for Registration {} send on email {}", otpEventDto.getCode(), otpEventDto.getSendTo());
    } else if(otpEventDto.getGrantType().equals(GrantType.PHONE_PASSWORD)) {
      smsServiceClient.sendSms(otpEventDto);
      log.info("Token for Registration {} send on sms {}", otpEventDto.getCode(), otpEventDto.getSendTo());
    } else {
      throw new BitrubyRuntimeExpection("Unknown OTP grant type");
    }
  }

  public void sendNotificationLoginEvent(OtpEventDto otpEventDto) {
    if(otpEventDto.getGrantType().equals(GrantType.EMAIL_PASSWORD)) {
      emailServiceClient.sendEmail(otpEventEmailMapper.map(otpEventDto));
      log.info("Token for Login {} send on email {}", otpEventDto.getCode(), otpEventDto.getSendTo());
    } else if(otpEventDto.getGrantType().equals(GrantType.PHONE_PASSWORD)) {
      smsServiceClient.sendSms(otpEventDto);
      log.info("Token for Login {} send on sms {}", otpEventDto.getCode(), otpEventDto.getSendTo());
    } else {
      throw new BitrubyRuntimeExpection("Unknown OTP grant type");
    }
  }

  public void sendNotificationRestorePasswordEvent(OtpEventDto otpEventDto) {
    if(otpEventDto.getGrantType().equals(GrantType.EMAIL_PASSWORD)) {
      emailServiceClient.sendEmail(otpEventEmailMapper.map(otpEventDto));
      log.info("Token for Restoring Password {} send on email {}", otpEventDto.getCode(), otpEventDto.getSendTo());
    } else if(otpEventDto.getGrantType().equals(GrantType.PHONE_PASSWORD)) {
      smsServiceClient.sendSms(otpEventDto);
      log.info("Token for Restoring Password {} send on sms {}", otpEventDto.getCode(), otpEventDto.getSendTo());
    } else {
      throw new BitrubyRuntimeExpection("Unknown OTP grant type");
    }
  }
}
