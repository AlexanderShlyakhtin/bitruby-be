package kg.bitruby.bybitintegrator.incomes.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConsumerProperties {

  @Value("${bitruby.kafka.topics.bybit.create-sub-account.name}")
  public String createSubAccountTopic;
}
