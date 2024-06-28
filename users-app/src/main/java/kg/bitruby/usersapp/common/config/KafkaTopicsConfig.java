package kg.bitruby.usersapp.common.config;

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

  @Value("${bitruby.kafka.topics.otp.login.name}")
  private String otpLoginTopic;
  @Value("${bitruby.kafka.topics.otp.login.numPartitions}")
  private int otpLoginTopicNumPartitions;
  @Value("${bitruby.kafka.topics.otp.login.replicationFactor}")
  private short otpLoginTopicReplicationFactor;

  @Value("${bitruby.kafka.topics.otp.registration.name}")
  private String otpRegistrationTopic;
  @Value("${bitruby.kafka.topics.otp.registration.numPartitions}")
  private int otpRegistrationTopicNumPartitions;
  @Value("${bitruby.kafka.topics.otp.registration.replicationFactor}")
  private short otpRegistrationTopicReplicationFactor;

  @Value("${bitruby.kafka.topics.otp.restore-password.name}")
  private String otpRestorePasswordTopic;
  @Value("${bitruby.kafka.topics.otp.restore-password.numPartitions}")
  private int otpRestorePasswordTopicNumPartitions;
  @Value("${bitruby.kafka.topics.otp.restore-password.replicationFactor}")
  private short otpRestorePasswordTopicReplicationFactor;

  @Value("${bitruby.kafka.topics.verification.events.name}")
  private String verificationEventsTopic;
  @Value("${bitruby.kafka.topics.verification.events.numPartitions}")
  private int verificationEventsTopicNumPartitions;
  @Value("${bitruby.kafka.topics.verification.events.replicationFactor}")
  private short verificationEventsTopicReplicationFactor;

  @Value("${bitruby.kafka.topics.verification.decisions.name}")
  private String verificationDecisionsTopic;
  @Value("${bitruby.kafka.topics.verification.decisions.numPartitions}")
  private int verificationDecisionsTopicNumPartitions;
  @Value("${bitruby.kafka.topics.verification.decisions.replicationFactor}")
  private short verificationDecisionsTopicReplicationFactor;

  @Value("${bitruby.kafka.topics.notifications.user-verification-status.name}")
  private String notificationsUserVerificationStatusDecisionsTopic;
  @Value("${bitruby.kafka.topics.notifications.user-verification-status.numPartitions}")
  private int notificationsUserVerificationStatusTopicNumPartitions;
  @Value("${bitruby.kafka.topics.notifications.user-verification-status.replicationFactor}")
  private short notificationsUserVerificationStatusTopicReplicationFactor;

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
  public NewTopic otpLoginTopic() {
    return new NewTopic(otpLoginTopic, otpLoginTopicNumPartitions,
        otpLoginTopicReplicationFactor);
  }

  @Bean
  public NewTopic otpRegistrationTopic() {
    return new NewTopic(otpRegistrationTopic, otpRegistrationTopicNumPartitions,
        otpRegistrationTopicReplicationFactor);
  }

  @Bean
  public NewTopic otpRestorePasswordTopic() {
    return new NewTopic(otpRestorePasswordTopic, otpRestorePasswordTopicNumPartitions,
        otpRestorePasswordTopicReplicationFactor);
  }

  @Bean
  public NewTopic verificationEventsTopic() {
    return new NewTopic(verificationEventsTopic, verificationEventsTopicNumPartitions,
        verificationEventsTopicReplicationFactor);
  }

  @Bean
  public NewTopic verificationDecisionsTopic() {
    return new NewTopic(verificationDecisionsTopic, verificationDecisionsTopicNumPartitions,
        verificationDecisionsTopicReplicationFactor);
  }

  @Bean
  public NewTopic notificationsUserVerificationStatusTopic() {
    return new NewTopic(notificationsUserVerificationStatusDecisionsTopic, notificationsUserVerificationStatusTopicNumPartitions,
        notificationsUserVerificationStatusTopicReplicationFactor);
  }

  @Bean
  public NewTopic createSubAccountTopic() {
    return new NewTopic(createSubAccountTopic, createSubAccountTopicNumPartitions,
        createSubAccountTopicReplicationFactor);
  }
}
