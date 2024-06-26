package kg.bitruby.usersapp.incomes.rest.controllers.webhooks;

import kg.bitruby.usersapp.api.VerificationApiDelegate;
import kg.bitruby.usersapp.api.model.Base;
import kg.bitruby.usersapp.api.model.VerificationDecision;
import kg.bitruby.usersapp.api.model.VerificationEvent;
import kg.bitruby.usersapp.core.verification.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class VerificationWebhooksController implements VerificationApiDelegate {

  private final VerificationService verificationService;

  /**
   * POST /public/verification/decision : Verification decision  hooks
   *
   * @param xAuthClient (required)
   * @param xSignature (required)
   * @param xHmacSignature (required)
   * @param verificationDecision Verification Event Request (optional)
   * @return response with no body (status code 201) or error (status code 400) or error (status
   * code 5XX)
   * @see VerificationApi#verificationDecision
   */
  @Override
  public ResponseEntity<Base> verificationDecision(UUID xAuthClient, String xSignature,
      String xHmacSignature, VerificationDecision verificationDecision) {
    //Из-за проблемы с request.getInputStream обработка запросов осуществляется в CheckHMacSignatureInterceptor
    return new ResponseEntity<>(new Base(true, OffsetDateTime.now()),
        HttpStatus.OK);
  }

  /**
   * POST /public/verification/event : Verification event hooks
   *
   * @param xAuthClient (required)
   * @param xSignature (required)
   * @param xHmacSignature (required)
   * @param verificationEvent Verification Event Request (optional)
   * @return response with no body (status code 201) or error (status code 400) or error (status
   * code 5XX)
   * @see VerificationApi#verificationEvent
   */
  @Override
  public ResponseEntity<Base> verificationEvent(UUID xAuthClient, String xSignature,
      String xHmacSignature, VerificationEvent verificationEvent) {
    //Из-за проблемы с request.getInputStream обработка запросов осуществляется в CheckHMacSignatureInterceptor
    return new ResponseEntity<>(new Base(true, OffsetDateTime.now()),
        HttpStatus.OK);
  }
}
