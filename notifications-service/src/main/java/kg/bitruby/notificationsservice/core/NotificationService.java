package kg.bitruby.notificationsservice.core;

import kg.bitruby.commonmodule.dto.kafkaevents.CommunicationType;
import kg.bitruby.commonmodule.dto.kafkaevents.OtpEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.notificationsservice.client.sendsay.api.model.SendEmailResult;
import kg.bitruby.notificationsservice.client.smstraffic.api.model.Reply;
import kg.bitruby.notificationsservice.outcomes.rest.sendsay.EmailServiceClient;
import kg.bitruby.notificationsservice.outcomes.rest.sendsay.mapper.OtpEventEmailMapper;
import kg.bitruby.notificationsservice.outcomes.rest.smstraffic.SmsServiceClient;
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
    CommunicationType communicationType = otpEventDto.getCommunicationType();
    if(communicationType.equals(CommunicationType.EMAIL_PASSWORD)) {
      SendEmailResult sendEmailResult =
          emailServiceClient.sendEmail(otpEventEmailMapper.map(otpEventDto));
      log.info("Token for Registration {} send on email {}. Result: {}", otpEventDto.getCode(), otpEventDto.getSendTo(), sendEmailResult.toString());
    } else if(communicationType.equals(CommunicationType.PHONE_PASSWORD)) {
      Reply sendSmsResult =
          smsServiceClient.sendSms(otpEventDto.getSendTo(), otpEventDto.getCode());
      log.info("Token for Registration {} send on sms {}. Result: {}", otpEventDto.getCode(), otpEventDto.getSendTo(), sendSmsResult.toString());
    } else {
      throw new BitrubyRuntimeExpection("Unknown OTP grant type");
    }
  }

  public void sendNotificationLoginEvent(OtpEventDto otpEventDto) {
    CommunicationType communicationType = otpEventDto.getCommunicationType();
    if(communicationType.equals(CommunicationType.EMAIL_PASSWORD)) {
      SendEmailResult sendEmailResult =
          emailServiceClient.sendEmail(otpEventEmailMapper.map(otpEventDto));
      log.info("Token for Login {} send on email {}. Result: {}", otpEventDto.getCode(), otpEventDto.getSendTo(), sendEmailResult.toString());
    } else if(communicationType.equals(CommunicationType.PHONE_PASSWORD)) {
      Reply sendSmsResult =
          smsServiceClient.sendSms(otpEventDto.getSendTo(), otpEventDto.getCode());
      log.info("Token for Login {} send on sms {}. Result: {}", otpEventDto.getCode(), otpEventDto.getSendTo(), sendSmsResult.toString());
    } else {
      throw new BitrubyRuntimeExpection("Unknown OTP grant type");
    }
  }

  public void sendNotificationRestorePasswordEvent(OtpEventDto otpEventDto) {
    CommunicationType communicationType = otpEventDto.getCommunicationType();
    if(otpEventDto.getCommunicationType().equals(CommunicationType.EMAIL_PASSWORD)) {
      SendEmailResult sendEmailResult =
          emailServiceClient.sendEmail(otpEventEmailMapper.map(otpEventDto));
      log.info("Token for Restoring Password {} send on email {}. Result: {}", otpEventDto.getCode(), otpEventDto.getSendTo(), sendEmailResult.toString());
    } else if(otpEventDto.getCommunicationType().equals(CommunicationType.PHONE_PASSWORD)) {
      Reply sendSmsResult =
          smsServiceClient.sendSms(otpEventDto.getSendTo(), otpEventDto.getCode());
      log.info("Token for Restoring Password {} send on sms {}. Result: {}", otpEventDto.getCode(), otpEventDto.getSendTo(), sendSmsResult.toString());
    } else {
      throw new BitrubyRuntimeExpection("Unknown OTP grant type");
    }
  }
}
