package kg.bitruby.authservice.common.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
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

  @Value("${bitruby.kafka.topics.otp.login.name}")
  private String otpLoginTopic;
  @Value("${bitruby.kafka.topics.otp.login.numPartitions}")
  private int otpLoginTopicNumPartitions;
  @Value("${bitruby.kafka.topics.otp.login.replicationFactor}")
  private short otpLoginTopicReplicationFactor;

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
}
