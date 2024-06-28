package kg.bitruby.bybitintegrator.incomes.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.bitruby.bybitintegrator.common.AppContextHolder;
import kg.bitruby.bybitintegrator.core.AccountService;
import kg.bitruby.commonmodule.dto.eventDto.CreateSubAccountDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
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
  private final AccountService accountService;

  @KafkaListener(topics = "#{kafkaConsumerProperties.createSubAccountTopic}")
  public void listenToVerificationEventsTopic(@Header(KafkaHeaders.RECEIVED_KEY) String key, String payload) {
    AppContextHolder.setRqUid(UUID.fromString(key));
    log.info("Kafka event value: {}", payload);
    CreateSubAccountDto event = mapObject(payload, CreateSubAccountDto.class);
    accountService.handleCreateSubAccountEvent(event);
  }

  public <T> T mapObject(String sourceObject, Class<T> targetClass) {
    try {
      return objectMapper.readValue(sourceObject, targetClass);
    } catch (Exception e) {
      throw new BitrubyRuntimeExpection("Error. Can't convert source file into target class");
    }
  }

}
