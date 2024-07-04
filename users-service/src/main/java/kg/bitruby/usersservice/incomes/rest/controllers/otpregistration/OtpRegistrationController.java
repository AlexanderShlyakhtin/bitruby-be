package kg.bitruby.usersservice.incomes.rest.controllers.otpregistration;

import kg.bitruby.usersservice.api.OtpRegistrationApiDelegate;
import kg.bitruby.usersservice.api.model.Base;
import kg.bitruby.usersservice.api.model.GenerateOtpCodeRegistration;
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
   * @param generateOtpCodeRegistration Generate OTP token for user registration (optional)
   * @return response with no body (status code 200) or error (status code 400) or error (status
   * code 5XX)
   * @see OtpRegistrationApi#generateOtpCodeForRegistration
   */
  @Override
  public ResponseEntity<Base> generateOtpCodeForRegistration(UUID xRequestId,
      GenerateOtpCodeRegistration generateOtpCodeRegistration) {
    return new ResponseEntity<>(otpService.generateOtpCodeForRegistration(generateOtpCodeRegistration), HttpStatus.OK);
  }
}
