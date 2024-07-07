package kg.bitruby.authservice.incomes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kg.bitruby.authservice.api.model.OtpLogin;
import kg.bitruby.authservice.api.model.OtpLoginResult;
import kg.bitruby.authservice.common.AppContextHolder;
import kg.bitruby.authservice.core.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OtpController {

  private final OtpService otpService;

  @Operation(
      operationId = "generateOtpCodeForLogin",
      summary = "Generate and send OTP code for login",
      tags = { "auth" },
      responses = {
          @ApiResponse(responseCode = "200", description = "error", content = {
              @Content(mediaType = "application/json", schema = @Schema(implementation = OtpLoginResult.class))
          })
      },
      security = {
          @SecurityRequirement(name = "basicAuth")
      }
  )
  @PostMapping(
      value = "/oauth2/otp",
      produces = { "application/json" },
      consumes = { "application/json" }
  )

  public ResponseEntity<OtpLoginResult> generateOtpCodeForLogin(
      @NotNull @Parameter(name = "x-request-id", description = "", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "x-request-id", required = true) UUID xRequestId,
      @Parameter(name = "OtpLogin", description = "Generate OTP token for user login") @Valid @RequestBody(required = false) OtpLogin otpLogin
  ) {
    AppContextHolder.setRqUid(xRequestId);
    return new ResponseEntity<>(otpService.generateOtpCodeForLogin(otpLogin), HttpStatus.OK);
  }
}
