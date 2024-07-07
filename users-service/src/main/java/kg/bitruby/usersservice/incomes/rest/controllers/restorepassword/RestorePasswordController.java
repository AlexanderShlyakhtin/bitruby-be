package kg.bitruby.usersservice.incomes.rest.controllers.restorepassword;

import kg.bitruby.usersservice.api.RestorePasswordApiDelegate;
import kg.bitruby.usersservice.api.model.*;
import kg.bitruby.usersservice.core.restorepassword.RestorePasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RestorePasswordController implements RestorePasswordApiDelegate {

  private final RestorePasswordService restorePasswordService;

  /**
   * POST /public/generate-otp/restore-password : Generate and send OTP code for Restoring the
   * password
   *
   * @param xRequestId (required)
   * @param otpCode Generate OTP token for restoring the password (optional)
   * @return response with no body (status code 200) or error (status code 400) or error (status
   * code 5XX)
   * @see OtpRestorePasswordApi#generateOtpCodeForRestoringPassword
   */
  @Override
  public ResponseEntity<RestorePasswordRequestOtpResult> generateOtpCodeForRestoringPassword(
      UUID xRequestId, OtpCode otpCode) {
    return new ResponseEntity<>(restorePasswordService.generateOtpCodeForRestoringPassword(otpCode), HttpStatus.OK);
  }

  /**
   * POST /public/generate-otp/restore-password/check : Check generatred OTP code to Restore user
   * the password
   *
   * @param xRequestId (required)
   * @param otpCodeRestorePassword Check OTP token for restoring the password (optional)
   * @return response with no body (status code 200) or error (status code 400) or error (status
   * code 5XX)
   * @see OtpRestorePasswordApi#checkOtpCodeForRestoringPassword
   */
  @Override
  public ResponseEntity<Base> checkOtpCodeForRestoringPassword(UUID xRequestId,
      OtpCodeRestorePassword otpCodeRestorePassword) {
    return new ResponseEntity<>(restorePasswordService.checkOtpCodeForRestoringPassword(otpCodeRestorePassword), HttpStatus.OK);
  }

  /**
   * POST /public/restore-password : Restore forgotten password Restore forgotten password
   *
   * @param xRequestId (required)
   * @param restorePassword Restore user password (optional)
   * @return response with no body (status code 200) or error (status code 400) or error (status
   * code 5XX)
   * @see RestorePasswordApi#restorePassword
   */
  @Override
  public ResponseEntity<Base> restorePassword(UUID xRequestId, RestorePassword restorePassword) {
    return new ResponseEntity<>(restorePasswordService.restorePassword(restorePassword), HttpStatus.OK);
  }
}
