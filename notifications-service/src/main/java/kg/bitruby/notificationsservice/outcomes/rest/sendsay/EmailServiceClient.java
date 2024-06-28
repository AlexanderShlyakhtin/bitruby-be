package kg.bitruby.notificationsservice.outcomes.rest.sendsay;

import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.notificationsservice.client.sendsay.api.EmailApi;
import kg.bitruby.notificationsservice.client.sendsay.api.model.SendEmail;
import kg.bitruby.notificationsservice.client.sendsay.api.model.SendEmailResult;
import kg.bitruby.notificationsservice.common.AppContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceClient {

  @Value("${bitruby.sendSay.apiLogin}")
  private String apiLogin;
  @Value("${bitruby.sendSay.active}")
  private Boolean active;

  private final EmailApi emailApi;

  public SendEmailResult sendEmail(SendEmail email) {
    if(Boolean.TRUE.equals(active)) {
      return safeCall( () ->  emailApi.sendEmail(apiLogin, AppContextHolder.getContextRequestId(), email));
    } else {
      log.info("Email service disable. Email payload {}", email.toString());
      return new SendEmailResult();
    }
  }

  private <R> R safeCall(SafeCallConsumer<R> safeCall) {
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
