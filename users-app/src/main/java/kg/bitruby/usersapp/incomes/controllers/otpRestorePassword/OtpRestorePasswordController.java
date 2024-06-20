package kg.bitruby.usersapp.incomes.controllers.otpRestorePassword;

import kg.bitruby.usersapp.api.OtpRestorePasswordApiDelegate;
import kg.bitruby.usersapp.api.model.Base;
import kg.bitruby.usersapp.api.model.OtpCode;
import kg.bitruby.usersapp.core.otp.OtpService;
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
}
