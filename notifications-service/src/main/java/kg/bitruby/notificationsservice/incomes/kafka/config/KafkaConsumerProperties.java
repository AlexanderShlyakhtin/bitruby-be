package kg.bitruby.notificationsservice.incomes.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConsumerProperties {

  @Value("${bitruby.kafka.topics.otp-login.name}")
  public String otpLoginTopic;

  @Value("${bitruby.kafka.topics.otp-registration.name}")
  public String otpRegistrationTopic;

  @Value("${bitruby.kafka.topics.otp-restore-password.name}")
  private String otpRestorePassword;
}
