package kg.bitruby.usersservice.incomes.rest.controllers.registration;

import kg.bitruby.usersservice.api.RegistrationApi;
import kg.bitruby.usersservice.api.RegistrationApiDelegate;
import kg.bitruby.usersservice.api.model.*;
import kg.bitruby.usersservice.core.registration.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RegistrationController implements RegistrationApiDelegate {

  private final RegistrationService registrationService;

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
    return new ResponseEntity<>(registrationService.generateOtpCodeForRegistration(generateOtpCodeRegistration), HttpStatus.OK);
  }

  /**
   * POST /public/registration/complete-registration : Complete registration of new user Complete
   * registration of the user by email confirmation
   *
   * @param xRequestId (required)
   * @param completeRegistration Generate OTP token for user login (optional)
   * @return response with no body (status code 200) or error (status code 400) or error (status
   * code 5XX)
   * @see RegistrationApi#completeRegistration
   */
  @Override
  public ResponseEntity<Base> completeRegistration(UUID xRequestId,
      CompleteRegistration completeRegistration) {
    return new ResponseEntity<>(registrationService.completeRegistration(completeRegistration), HttpStatus.OK);
  }

  /**
   * POST /public/registration : Register new user
   *
   * @param xRequestId (required)
   * @param newUser Registration body of new user\&quot; (optional)
   * @return error (status code 201) or error (status code 400) or error (status code 5XX)
   * @see RegistrationApi#registerUser
   */
  @Override
  public ResponseEntity<RegisterNewUserResult> registerUser(UUID xRequestId, NewUser newUser) {
    return new ResponseEntity<>(registrationService.registerUser(newUser), HttpStatus.CREATED);
  }
}
