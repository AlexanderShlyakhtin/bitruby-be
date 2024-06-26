package kg.bitruby.usersapp.incomes.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.bitruby.commonmodule.dto.eventDto.VerificationDecisionDto;
import kg.bitruby.commonmodule.dto.eventDto.VerificationEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersapp.common.AppContextHolder;
import kg.bitruby.usersapp.core.users.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

  private final ObjectMapper objectMapper;
  private final UsersService usersService;

  @KafkaListener(topics = "#{kafkaConsumerProperties.verificationEvents}")
  public void listenToVerificationEventsTopic(@Header(KafkaHeaders.RECEIVED_KEY) String key, String payload) {
    AppContextHolder.setRqUid(UUID.fromString(key));
    log.info("Kafka event value: {}", payload);
    VerificationEventDto event = mapObject(payload, VerificationEventDto.class);
    usersService.handleVerificationEvent(event);
  }

  @KafkaListener(topics = "#{kafkaConsumerProperties.verificationDecisions}")
  public void listenToVerificationDecisionsTopic(@Header(KafkaHeaders.RECEIVED_KEY) String key, String payload) {
    AppContextHolder.setRqUid(UUID.fromString(key));
    log.info("Kafka event value: {}", payload);
    VerificationDecisionDto event = mapObject(payload, VerificationDecisionDto.class);
    usersService.handleVerificationDecision(event);
  }

  public <T> T mapObject(String sourceObject, Class<T> targetClass) {
    try {
      return objectMapper.readValue(sourceObject, targetClass);
    } catch (Exception e) {
      throw new BitrubyRuntimeExpection("Error. Can't convert source file into target class");
    }
  }

}
