package kg.bitruby.usersapp.incomes.controllers.otpLogin;

import kg.bitruby.usersapp.api.OtpLoginApiDelegate;
import kg.bitruby.usersapp.api.model.Base;
import kg.bitruby.usersapp.api.model.OtpCode;
import kg.bitruby.usersapp.api.model.OtpCodeLogin;
import kg.bitruby.usersapp.core.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OtpLoginController implements OtpLoginApiDelegate {

  private final OtpService otpService;

  /**
   * POST /public/generate-otp/login : Generate and send OTP code for login
   *
   * @param xRequestId (required)
   * @param otpCodeLogin Generate OTP token for user login (optional)
   * @return response with no body (status code 200) or error (status code 400) or error (status
   * code 5XX)
   * @see OtpLoginApiDelegate#generateOtpCodeForLogin
   */
  @Override
  public ResponseEntity<Base> generateOtpCodeForLogin(UUID xRequestId, OtpCodeLogin otpCodeLogin) {
    return new ResponseEntity<>(otpService.generateOtpCodeForLogin(otpCodeLogin), HttpStatus.OK);
  }
}
