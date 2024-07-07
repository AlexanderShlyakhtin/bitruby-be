package kg.bitruby.usersservice.outcomes.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.bitruby.commonmodule.dto.kafkaevents.*;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersservice.common.AppContextHolder;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Value("${bitruby.kafka.topics.otp.registration.name}")
  private String otpRegistration;

  @Value("${bitruby.kafka.topics.otp.restore-password.name}")
  private String otpRestorePassword;

  @Value("${bitruby.kafka.topics.verification.decisions.name}")
  private String verificationDecisions;

  @Value("${bitruby.kafka.topics.verification.events.name}")
  private String verificationEvents;

  @Value("${bitruby.kafka.topics.notifications.user-verification-status.name}")
  private String notificationsUserVerificationStatus;

  @Value("${bitruby.kafka.topics.bybit.create-sub-account.name}")
  private String createSubAccountTopic;

  public void emitOtpRegistrationEventMessage(OtpEventDto event) {
    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
        otpRegistration,
        null,
        AppContextHolder.getContextRequestId().toString(),
        convertObjectToJson(event)
    );
    kafkaTemplate.send(producerRecord);
  }

  public void emitOtpRestorePasswordEventMessage(OtpEventDto event) {
    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
        otpRestorePassword,
        null,
        AppContextHolder.getContextRequestId().toString(),
        convertObjectToJson(event)
    );
    kafkaTemplate.send(producerRecord);
  }


  public void emitVerificationDecisionMessage(VerificationDecisionDto event) {
    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
        verificationDecisions,
        null,
        AppContextHolder.getContextRequestId().toString(),
        convertObjectToJson(event)
    );
    kafkaTemplate.send(producerRecord);
  }

  public void emitVerificationEventMessage(VerificationEventDto event) {
    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
        verificationEvents,
        null,
        AppContextHolder.getContextRequestId().toString(),
        convertObjectToJson(event)
    );
    kafkaTemplate.send(producerRecord);
  }

  public void emitCreateSubAccountMessage(CreateSubAccountDto event) {
    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
        createSubAccountTopic,
        null,
        AppContextHolder.getContextRequestId().toString(),
        convertObjectToJson(event)
    );
    kafkaTemplate.send(producerRecord);
  }

  public void emitNotificationsUserVerificationStatusMessage(
      NotificationUserVerificationStatusChangedDto event) {
    Iterable<Header> headers = Arrays.asList(
        new RecordHeader("user-id", event.getUserId().toString().getBytes())
    );
    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
        notificationsUserVerificationStatus,
        null,
        AppContextHolder.getContextRequestId().toString(),
        convertObjectToJson(event),
        headers
    );
    kafkaTemplate.send(producerRecord);
  }

  private String convertObjectToJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new BitrubyRuntimeExpection("Kafka event serialization error", e); // or throw an exception if you prefer
    }
  }
}
