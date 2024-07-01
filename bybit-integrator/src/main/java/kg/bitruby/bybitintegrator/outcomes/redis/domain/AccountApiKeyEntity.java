package kg.bitruby.bybitintegrator.outcomes.redis.domain;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kg.bitruby.usersapp.client.bybit.api.model.CreateSubApiResultResultPermissions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@RedisHash(value = "AccountApiKey", timeToLive = 10000L)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountApiKeyEntity {

  private UUID id;

  private String note;

  private String apiKey;

  private Integer readOnly;

  private String secret;

  @Enumerated(EnumType.STRING)
  private CreateSubApiResultResultPermissions permissions;
}
