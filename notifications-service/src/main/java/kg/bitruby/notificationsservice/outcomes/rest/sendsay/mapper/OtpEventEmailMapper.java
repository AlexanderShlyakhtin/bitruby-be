package kg.bitruby.notificationsservice.outcomes.rest.sendsay.mapper;

import kg.bitruby.commonmodule.dto.eventDto.OtpEventDto;
import kg.bitruby.notificationsservice.client.sendsay.api.model.Letter;
import kg.bitruby.notificationsservice.client.sendsay.api.model.LetterMessage;
import kg.bitruby.notificationsservice.client.sendsay.api.model.SendEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OtpEventEmailMapper {

  @Value("${bitruby.sendSay.emailFrom}")
  private String emailFrom;
  @Value("${bitruby.sendSay.apiKey}")
  private String apiKey;

  public SendEmail map(OtpEventDto otpEventDto) {
    SendEmail sendEmail = new SendEmail();

    Letter letter = new Letter();
    letter.setFromEmail(emailFrom);
    letter.setSubject("OTP");
    LetterMessage letterMessage = new LetterMessage();
    letterMessage.setHtml(otpEventDto.getCode());
    letter.setMessage(letterMessage);

    sendEmail.setEmail(otpEventDto.getSendTo());
    sendEmail.setLetter(letter);
    sendEmail.setAction(SendEmail.ActionEnum.ISSUE_SEND);
    sendEmail.setSendwhen(SendEmail.SendwhenEnum.NOW);
    //TODO: Define group
    sendEmail.setGroup("personal");
    sendEmail.setApikey(apiKey);

    return sendEmail;
  }
}
