package kg.bitruby.usersservice.incomes.rest.controllers.otpRegistration;

import kg.bitruby.usersservice.api.OtpRegistrationApiDelegate;
import kg.bitruby.usersservice.api.model.Base;
import kg.bitruby.usersservice.api.model.OtpCode;
import kg.bitruby.usersservice.core.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OtpRegistrationController implements OtpRegistrationApiDelegate {

  private final OtpService otpService;

  /**
   * POST /public/generate-otp/registration : Generate and send OTP code for Registration
   *
   * @param xRequestId (required)
   * @param otpCode Generate OTP token for user registration (optional)
   * @return response with no body (status code 200) or error (status code 400) or error (status
   * code 5XX)
   * @see OtpRegistrationApi#generateOtpCodeForRegistration
   */
  @Override
  public ResponseEntity<Base> generateOtpCodeForRegistration(UUID xRequestId, OtpCode otpCode) {
    return new ResponseEntity<>(otpService.generateOtpCodeForRegistration(otpCode), HttpStatus.OK);
  }
}
