package kg.bitruby.bybitintegrator.common.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicsConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${bitruby.kafka.topics.bybit.create-sub-account.name}")
  private String createSubAccountTopic;
  @Value("${bitruby.kafka.topics.bybit.create-sub-account.numPartitions}")
  private int createSubAccountTopicNumPartitions;
  @Value("${bitruby.kafka.topics.bybit.create-sub-account.replicationFactor}")
  private short createSubAccountTopicReplicationFactor;

  // Configure KafkaAdmin bean
  @Bean
  public KafkaAdmin kafkaAdmin() {
    return new KafkaAdmin(kafkaConfig());
  }

  // Configure Kafka properties
  @Bean
  public Map<String, Object> kafkaConfig() {
    Map<String, Object> config = new HashMap<>();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    return config;
  }

  @Bean
  public NewTopic createSubAccountTopic() {
    return new NewTopic(createSubAccountTopic, createSubAccountTopicNumPartitions,
        createSubAccountTopicReplicationFactor);
  }
}
