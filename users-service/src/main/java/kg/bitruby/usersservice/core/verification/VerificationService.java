package kg.bitruby.usersservice.core.verification;

import kg.bitruby.commonmodule.dto.events.VerificationDecisionDto;
import kg.bitruby.commonmodule.dto.events.VerificationDecisionStatus;
import kg.bitruby.commonmodule.dto.events.VerificationEventAction;
import kg.bitruby.commonmodule.dto.events.VerificationEventDto;
import kg.bitruby.usersservice.api.model.Base;
import kg.bitruby.usersservice.api.model.VerificationDecision;
import kg.bitruby.usersservice.api.model.VerificationEvent;
import kg.bitruby.usersservice.common.AppContextHolder;
import kg.bitruby.usersservice.outcomes.kafka.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class VerificationService {

  private final KafkaProducerService kafkaProducerService;

  public Base verificationDecision(VerificationDecision verificationDecision) {
    AppContextHolder.setRqUid(verificationDecision.getVerification().getId());
    kafkaProducerService.emitVerificationDecisionMessage(VerificationDecisionDto
        .builder()
          .userId(verificationDecision.getVerification().getPerson().getIdNumber())
          .id(verificationDecision.getVerification().getId())
          .status(VerificationDecisionStatus.fromValue(verificationDecision.getVerification().getStatus().getValue()))
        .build());
    return new Base(true, OffsetDateTime.now());
  }

  public Base verificationEvent(VerificationEvent verificationEvent) {
    AppContextHolder.setRqUid(verificationEvent.getId());
    kafkaProducerService.emitVerificationEventMessage(VerificationEventDto
        .builder()
          .id(verificationEvent.getId())
          .attemptId(verificationEvent.getAttemptId())
          .action(VerificationEventAction.fromValue(verificationEvent.getAction().getValue()))
          .feature(verificationEvent.getFeature())
          .code(verificationEvent.getCode().getValue())
          .vendorData(verificationEvent.getVendorData())
        .build());
    return new Base(true, OffsetDateTime.now());
  }

}
