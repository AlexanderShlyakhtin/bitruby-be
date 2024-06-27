package kg.bitruby.usersapp.incomes.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConsumerProperties {

  @Value("${bitruby.kafka.topics.verification.events.name}")
  public String verificationEvents;

  @Value("${bitruby.kafka.topics.verification.decisions.name}")
  public String verificationDecisions;
}
