package com.bitruby.usersapp.incomes;

import com.bitruby.usersapp.core.users.UsersService;
import com.bitruby.usersapp.api.UsersApiDelegate;
import com.bitruby.usersapp.api.model.RegisterUser;
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
   * POST /public/registration/complete-registration/{id} : Complete registration of new user
   * Complete registration of the user by email confirmation
   *
   * @param id (required)
   * @return response with no body (status code 200) or error (status code 500)
   * @see UsersApi#completeRegistration
   */
  @Override
  public ResponseEntity<Void> completeRegistration(UUID id) {
    usersService.completeRegistration(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * GET /secured : Test
   *
   * @return response with no body (status code 200)
   * @see UsersApi#testAuth
   */
  @Override
  public ResponseEntity<Void> testAuth() {
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * GET /public : Test
   *
   * @return response with no body (status code 200)
   * @see UsersApi#testPublic
   */
  @Override
  public ResponseEntity<Void> testPublic() {
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
