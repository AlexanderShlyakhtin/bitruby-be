package com.bitruby.usersapp.incomes;

import com.bitruby.usersapp.api.UsersApiDelegate;
import com.bitruby.usersapp.api.model.OtpCodeCheck;
import com.bitruby.usersapp.api.model.RegisterUser;
import com.bitruby.usersapp.core.users.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApiDelegate {

  private final UsersService usersService;

  /**
   * POST /users/public/registration : Register new user
   *
   * @param registerUser Registration body of new user\&quot; (optional)
   * @return response with no body (status code 201) or error (status code 500)
   * @see UsersApi#registerUser
   */
  @Override
  public ResponseEntity<Void> registerUser(RegisterUser registerUser) {
    usersService.registerUser(registerUser);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  /**
   * POST /public/registration/complete-registration : Complete registration of new user Complete
   * registration of the user by email confirmation
   *
   * @param otpCodeCheck Generate OTP token for user login (optional)
   * @return response with no body (status code 200) or error (status code 500)
   * @see UsersApi#completeRegistration
   */
  @Override
  public ResponseEntity<Void> completeRegistration(OtpCodeCheck otpCodeCheck) {
    usersService.completeRegistration(otpCodeCheck);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
