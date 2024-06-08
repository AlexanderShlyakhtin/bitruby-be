package com.bitruby.usersapp.incomes;

import com.bitruby.usersapp.api.OtpApiDelegate;
import com.bitruby.usersapp.api.model.OtpCode;
import com.bitruby.usersapp.api.model.OtpCodeLogin;
import com.bitruby.usersapp.core.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OtpController implements OtpApiDelegate {

  private final OtpService otpService;


  /**
   * POST /public/generate-otp/login : Generate and send OTP code for login
   *
   * @param otpCodeLogin Generate OTP token for user login (optional)
   * @return response with no body (status code 200) or error (status code 400) or error (status
   * code 500)
   * @see OtpApi#generateOtpCodeForLogin
   */
  @Override
  public ResponseEntity<Void> generateOtpCodeForLogin(OtpCodeLogin otpCodeLogin) {
    otpService.generateOtpCodeForLogin(otpCodeLogin);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * POST /public/generate-otp/registration : Generate and send OTP code for Registration
   *
   * @param otpCode Generate OTP token for user login (optional)
   * @return response with no body (status code 200) or error (status code 400) or error (status
   * code 500)
   * @see OtpApi#generateOtpCodeForRegistration
   */
  @Override
  public ResponseEntity<Void> generateOtpCodeForRegistration(OtpCode otpCode) {
    otpService.generateOtpCodeForRegistration(otpCode);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
