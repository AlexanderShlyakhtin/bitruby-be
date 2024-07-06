package kg.bitruby.usersservice.outcomes.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(
    basePackages = "kg.bitruby.usersservice.outcomes.redis.repository"
)
public class RedisConfig {

  @Bean
  RedisConnectionFactory lettuceConnectionFactory() {
    return new LettuceConnectionFactory();
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(lettuceConnectionFactory());

    // Key serializer
    template.setKeySerializer(new StringRedisSerializer());
    // Hash key serializer
    template.setHashKeySerializer(new StringRedisSerializer());

    // Value serializer
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    // Initialize the template
    template.afterPropertiesSet();

    return template;
  }
}
