package kg.bitruby.usersapp.core.verification;

import kg.bitruby.commonmodule.dto.eventDto.VerificationDecisionDto;
import kg.bitruby.commonmodule.dto.eventDto.VerificationDecisionStatus;
import kg.bitruby.commonmodule.dto.eventDto.VerificationEventAction;
import kg.bitruby.commonmodule.dto.eventDto.VerificationEventDto;
import kg.bitruby.usersapp.api.model.Base;
import kg.bitruby.usersapp.api.model.VerificationDecision;
import kg.bitruby.usersapp.api.model.VerificationEvent;
import kg.bitruby.usersapp.common.AppContextHolder;
import kg.bitruby.usersapp.outcomes.kafka.service.KafkaProducerService;
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
