package kg.bitruby.bybitintegratorservice.outcomes.rest.bybit.api;

import kg.bitruby.bybitintegratorservice.client.bybit.api.AccountApi;
import kg.bitruby.bybitintegratorservice.client.bybit.api.model.CreateSubAccountResult;
import kg.bitruby.bybitintegratorservice.client.bybit.api.model.CreateSubApiRequest;
import kg.bitruby.bybitintegratorservice.client.bybit.api.model.CreateSubApiResult;
import kg.bitruby.bybitintegratorservice.client.bybit.api.model.CreateSubMember;
import kg.bitruby.bybitintegratorservice.outcomes.rest.bybit.service.ByBitSignatureService;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;


@Service
@RequiredArgsConstructor
@Slf4j
public class ByBitApiClient {

  @Value("${bitruby.bybit.bybit-api-key}")
  private String apiKey;

  @Value("${bitruby.bybit.recvWindow}")
  private Long recvWindow;

  @Value("${bitruby.bybit.active}")
  private Boolean active;

  private final AccountApi accountApi;
  private final ByBitSignatureService byBitSignatureService;

  public CreateSubApiResult createSubAccountApiKey(CreateSubApiRequest createSubApiRequest) {
    if(Boolean.TRUE.equals(active)) {
      long epochMilli = System.currentTimeMillis();
      return safeCall( () ->  accountApi.createSubAccountApiKey(byBitSignatureService.signPost(createSubApiRequest, epochMilli), apiKey,
          epochMilli, recvWindow, createSubApiRequest));
    } else {
      return new CreateSubApiResult();
    }
  }

  public CreateSubAccountResult createSubAccount(CreateSubMember createSubMember) {
    if(Boolean.TRUE.equals(active)) {
      long epochMilli = System.currentTimeMillis();
      return safeCall(() -> accountApi.createSubAccount(
          byBitSignatureService.signPost(createSubMember, epochMilli), apiKey, epochMilli,
          recvWindow, createSubMember));
    } else {
      return new CreateSubAccountResult();
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
                "Error. Can't process request to ByBit server. Error message: %s",
                e.getMessage()),
            e);
      }
      return result;
    }

    R acceptThrows();
  }

}
