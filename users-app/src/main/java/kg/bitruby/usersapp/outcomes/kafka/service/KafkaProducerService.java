package kg.bitruby.usersapp.outcomes.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.bitruby.commonmodule.dto.eventDto.OtpEventDto;
import kg.bitruby.usersapp.common.AppContextHolder;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
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

  @Value("${bitruby.kafka.topics.otp-login.name}")
  private String otpLogin;

  @Value("${bitruby.kafka.topics.otp-registration.name}")
  private String otpRegistration;

  @Value("${bitruby.kafka.topics.otp-restore-password.name}")
  private String otpRestorePassword;

  public void emitOtpLoginEventMessage(OtpEventDto event) {
    Iterable<Header> headers = Arrays.asList(
        new RecordHeader("x-request-id", AppContextHolder.getContextRequestId().toString().getBytes())
    );
    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
        otpLogin,
        null,
        AppContextHolder.getContextRequestId().toString(),
        convertObjectToJson(event),
        headers
    );
    kafkaTemplate.send(producerRecord);
  }

  public void emitOtpRegistrationEventMessage(OtpEventDto event) {
    Iterable<Header> headers = Arrays.asList(
        new RecordHeader("x-request-id", AppContextHolder.getContextRequestId().toString().getBytes())
    );
    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
        otpRegistration,
        null,
        AppContextHolder.getContextRequestId().toString(),
        convertObjectToJson(event),
        headers
    );
    kafkaTemplate.send(producerRecord);
  }

  public void emitOtpRestorePasswordEventMessage(OtpEventDto event) {
    Iterable<Header> headers = Arrays.asList(
        new RecordHeader("x-request-id", AppContextHolder.getContextRequestId().toString().getBytes())
    );
    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
        otpRestorePassword,
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
