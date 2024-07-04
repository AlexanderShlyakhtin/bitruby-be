package kg.bitruby.bybitintegratorservice.incomes.rest.account;

import kg.bitruby.bybitintegratorservice.api.AccountApiDelegate;
import kg.bitruby.bybitintegratorservice.api.model.AccountApiKey;
import kg.bitruby.bybitintegratorservice.core.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountApiDelegate {

  private final AccountService accountService;

  /**
   * GET /account/{id}/api-key : Get Account API key Get User Account API key.
   *
   * @param id (required)
   * @return Get Account API key (status code 200) or Successfully created sub-user API key. (status
   * code 4XX) or Successfully created sub-user API key. (status code 5XX)
   * @see AccountApi#getApiKeySubAccount
   */
  @Override
  public ResponseEntity<AccountApiKey> getApiKeySubAccount(UUID id) {
    return new ResponseEntity<>(accountService.getApiKeySubAccount(id), HttpStatus.OK);
  }
}
