package kg.bitruby.usersapp.outcomes.rest.veriffClient.api;

import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersapp.client.veriff.api.VerificationApi;
import kg.bitruby.usersapp.client.veriff.api.model.NewSession;
import kg.bitruby.usersapp.client.veriff.api.model.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Supplier;


@Service
@RequiredArgsConstructor
@Slf4j
public class VeriffApiClient {

  @Value("${bitruby.verification.api-key}")
  private UUID apiKey;

  private final VerificationApi verificationApi;
  public Session createSession(NewSession newSession) {
    return safeCall( () ->  verificationApi.verificationEvent(apiKey, newSession));
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
