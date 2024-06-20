package kg.bitruby.notificationsservice.incomes.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.bitruby.commonmodule.dto.eventDto.OtpEventDto;
import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.notificationsservice.common.AppContextHolder;
import kg.bitruby.notificationsservice.core.NotificationService;
import kg.bitruby.notificationsservice.incomes.kafka.config.KafkaConsumerProperties;
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
  private final NotificationService notificationService;
  private final KafkaConsumerProperties kafkaConsumerProperties;

  @KafkaListener(topics = "#{kafkaConsumerProperties.otpLoginTopic}")
  public void listenToOtpLoginTopic(@Header(KafkaHeaders.RECEIVED_KEY) String key, String event) {
    AppContextHolder.setContext(UUID.fromString(key));
    log.info("Kafka event value: {}", event);
    OtpEventDto otpEventDto = mapToDtoNewMerchant(event);
    notificationService.sendNotificationLoginEvent(otpEventDto);
  }

  @KafkaListener(topics = "#{kafkaConsumerProperties.otpRegistrationTopic}")
  public void listenToOtpRegistrationTopic(@Header(KafkaHeaders.RECEIVED_KEY) String key, String event) {
    AppContextHolder.setContext(UUID.fromString(key));
    log.info("Kafka event value: {}", event);
    OtpEventDto otpEventDto = mapToDtoNewMerchant(event);
    notificationService.sendNotificationRegistrationEvent(otpEventDto);
  }

//  @KafkaListener(topics = "#{kafkaConsumerProperties.otpRestorePassword }")
//  public void listenToOtpRestorePasswordTopic(@Header(KafkaHeaders.RECEIVED_KEY) String key, String event) {
//    AppContextHolder.setContext(UUID.fromString(key));
//    log.info("Kafka event value: {}", event);
//    OtpEventDto otpEventDto = mapToDtoNewMerchant(event);
//    notificationService.sendNotificationRestorePasswordEvent(otpEventDto);
//  }

  private OtpEventDto mapToDtoNewMerchant(String s) {
    try {
      return objectMapper.readValue(s, OtpEventDto.class);
    } catch (Exception e) {
      throw new BitrubyRuntimeExpection("Deserialization OTP event error", e);
    }
  }

}
