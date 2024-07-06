package kg.bitruby.usersservice.outcomes.redis.domain;

import kg.bitruby.commonmodule.domain.PreUserRegistrationBase;
import org.springframework.data.redis.core.RedisHash;


@RedisHash(value = "PreUserRegistration", timeToLive = 10000L)
public class PreUserRegistration extends PreUserRegistrationBase {
}
