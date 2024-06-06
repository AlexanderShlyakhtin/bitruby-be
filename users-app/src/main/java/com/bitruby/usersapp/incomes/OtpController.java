package com.bitruby.usersapp.incomes;

import com.bitruby.usersapp.api.OtpApiDelegate;
import com.bitruby.usersapp.api.model.OtpCode;
import com.bitruby.usersapp.api.model.OtpCodeCheck;
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
   * POST /public/generate-otp : Generate and send OTP code for login
   *
   * @param otpCodeCheck Generate OTP token for user login (optional)
   * @return response with no body (status code 200) or error (status code 400) or error (status
   * code 500)
   * @see OtpApi#generateOtpCodeForLogin
   */
  @Override
  public ResponseEntity<Void> generateOtpCodeForLogin(OtpCode otpCodeCheck) {
    otpService.generateOtpCodeForLogin(otpCodeCheck);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
