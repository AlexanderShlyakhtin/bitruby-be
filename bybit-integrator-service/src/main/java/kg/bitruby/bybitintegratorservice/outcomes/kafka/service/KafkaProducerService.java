package kg.bitruby.bybitintegratorservice.outcomes.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.bitruby.bybitintegratorservice.common.AppContextHolder;
import kg.bitruby.commonmodule.dto.kafkaevents.UserStatusEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Value("${bitruby.kafka.topics.users.user-status.name}")
  private String userAccountStatusChangedTopic;

  public void emitUserAccountStatusChangedEventMessage(UserStatusEventDto event) {
    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
        userAccountStatusChangedTopic,
        null,
        AppContextHolder.getContextRequestId().toString(),
        convertObjectToJson(event)
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
