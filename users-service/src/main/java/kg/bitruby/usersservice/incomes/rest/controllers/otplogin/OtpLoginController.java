package kg.bitruby.usersservice.incomes.rest.controllers.otplogin;

import kg.bitruby.usersservice.api.OtpLoginApiDelegate;
import kg.bitruby.usersservice.api.model.GenerateOtpCodeLoginResult;
import kg.bitruby.usersservice.api.model.OtpCodeLogin;
import kg.bitruby.usersservice.core.otp.OtpService;
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
   * @return error (status code 200) or error (status code 400) or error (status code 5XX)
   * @see OtpLoginApi#generateOtpCodeForLogin
   */
  @Override
  public ResponseEntity<GenerateOtpCodeLoginResult> generateOtpCodeForLogin(UUID xRequestId,
      OtpCodeLogin otpCodeLogin) {
    return new ResponseEntity<>(otpService.generateOtpCodeForLogin(otpCodeLogin), HttpStatus.OK);

  }
}
