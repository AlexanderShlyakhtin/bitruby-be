package kg.bitruby.usersapp.incomes.controllers.users;

import kg.bitruby.usersapp.api.UsersApiDelegate;
import kg.bitruby.usersapp.api.model.Base;
import kg.bitruby.usersapp.api.model.NewUser;
import kg.bitruby.usersapp.api.model.OtpCodeCheck;
import kg.bitruby.usersapp.core.users.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApiDelegate {

  private final UsersService usersService;


  /**
   * POST /public/registration/complete-registration : Complete registration of new user Complete
   * registration of the user by email confirmation
   *
   * @param xRequestId (required)
   * @param otpCodeCheck Generate OTP token for user login (optional)
   * @return response with no body (status code 200) or error (status code 400) or error (status
   * code 5XX)
   * @see UsersApi#completeRegistration
   */
  @Override
  public ResponseEntity<Base> completeRegistration(UUID xRequestId, OtpCodeCheck otpCodeCheck) {
    usersService.completeRegistration(otpCodeCheck);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * POST /public/registration : Register new user
   *
   * @param xRequestId (required)
   * @param newUser Registration body of new user\&quot; (optional)
   * @return response with no body (status code 201) or error (status code 400) or error (status
   * code 5XX)
   * @see UsersApi#registerUser
   */
  @Override
  public ResponseEntity<Base> registerUser(UUID xRequestId, NewUser newUser) {
    usersService.registerUser(newUser);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

}
