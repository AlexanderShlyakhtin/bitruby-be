package kg.bitruby.usersservice.incomes.rest.controllers.otpRestorePassword;

import kg.bitruby.usersservice.api.OtpRestorePasswordApiDelegate;
import kg.bitruby.usersservice.api.model.Base;
import kg.bitruby.usersservice.api.model.OtpCode;
import kg.bitruby.usersservice.api.model.OtpCodeRestorePassword;
import kg.bitruby.usersservice.core.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OtpRestorePasswordController implements OtpRestorePasswordApiDelegate {

  private final OtpService otpService;


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
  public ResponseEntity<Base> generateOtpCodeForRestoringPassword(UUID xRequestId,
      OtpCode otpCode) {
    return new ResponseEntity<>(otpService.generateOtpCodeForRestoringPassword(otpCode), HttpStatus.OK);
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
    return new ResponseEntity<>(otpService.checkOtpCodeForRestoringPassword(otpCodeRestorePassword), HttpStatus.OK);
  }
}
