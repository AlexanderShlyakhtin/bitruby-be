package kg.bitruby.notificationsservice.outcomes.sms;

import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.notificationsservice.client.smstraffic.api.SmsApi;
import kg.bitruby.notificationsservice.client.smstraffic.api.model.SendSmsResult;
import kg.bitruby.notificationsservice.outcomes.email.EmailServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceClient {

  @Value("${bitruby.smsTraffic.login}")
  private String login;

  @Value("${bitruby.smsTraffic.password}")
  private String password;

  @Value("${bitruby.smsTraffic.active}")
  private String active;

  private final SmsApi smsApi;

  public SendSmsResult sendSms(String phone, String message) {
    if(Boolean.TRUE.equals(active)) {
      return safeCall( () ->  smsApi.multiPhpPost(
          login,
          password,
          phone,
          message,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null
          ));
    } else {
      log.info("Sms service disable. Sms payload: Phone: {}, Message: {}", phone, message);
      return new SendSmsResult();
    }
  }

  private <R> R safeCall(EmailServiceClient.SafeCallConsumer<R> safeCall) {
    return safeCall.get();
  }

  @FunctionalInterface
  public interface SafeCallConsumer<R> extends Supplier<R> {
    @Override
    default R get() {
      R result = null;
      try {
        result = acceptThrows();
      } catch (Exception e) {
        throw new BitrubyRuntimeExpection(
            String.format(
                "Error. Can't process request to Veriff server. Error message: %s",
                e.getMessage()),
            e);
      }
      return result;
    }

    R acceptThrows();
  }
}
