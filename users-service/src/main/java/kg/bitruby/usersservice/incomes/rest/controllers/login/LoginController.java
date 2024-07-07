package kg.bitruby.usersservice.incomes.rest.controllers.login;

import kg.bitruby.usersservice.api.LoginApiDelegate;
import kg.bitruby.usersservice.api.model.GenerateOtpCodeLoginResult;
import kg.bitruby.usersservice.api.model.OtpCodeLogin;
import kg.bitruby.usersservice.core.login.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LoginController implements LoginApiDelegate {

  private final LoginService loginService;

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
    return new ResponseEntity<>(loginService.generateOtpCodeForLogin(otpCodeLogin), HttpStatus.OK);

  }
}
