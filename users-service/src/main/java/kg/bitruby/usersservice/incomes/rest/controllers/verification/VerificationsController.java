package kg.bitruby.usersservice.incomes.rest.controllers.verification;

import kg.bitruby.usersservice.api.VerificationApiDelegate;
import kg.bitruby.usersservice.api.model.Base;
import kg.bitruby.usersservice.api.model.UserForm;
import kg.bitruby.usersservice.api.model.UserVerification;
import kg.bitruby.usersservice.core.restorepassword.RestorePasswordService;
import kg.bitruby.usersservice.core.verification.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class VerificationsController implements VerificationApiDelegate {

  private final VerificationService verificationService;
  private final RestorePasswordService restorePasswordService;

  /**
   * POST /secured/form/{user-id} : Apply user form Apply user form
   *
   * @param xRequestId (required)
   * @param userId (required)
   * @param userForm Generate OTP token for user login (optional)
   * @return response with no body (status code 200) or error (status code 400) or error (status
   * code 5XX)
   * @see UsersApi#applyUserForm
   */
  @Override
  public ResponseEntity<Base> applyUserForm(UUID xRequestId, UserForm userForm) {
    return new ResponseEntity<>(verificationService.applyUserForm(userForm), HttpStatus.OK);
  }

  /**
   * GET /secured/verification/{user-id} : Get user verification data Get user verification data
   *
   * @param xRequestId (required)
   * @param userId (required)
   * @return error (status code 200) or error (status code 400) or error (status code 5XX)
   * @see UsersApi#getUserVerificationData
   */
  @Override
  public ResponseEntity<UserVerification> getUserVerificationData(UUID xRequestId) {
    return new ResponseEntity<>(verificationService.getUserVerificationData(), HttpStatus.OK);
  }
}
