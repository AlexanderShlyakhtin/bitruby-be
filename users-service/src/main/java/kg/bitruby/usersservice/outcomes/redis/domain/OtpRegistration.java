package kg.bitruby.usersservice.outcomes.redis.domain;

import kg.bitruby.commonmodule.domain.TokenBase;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "OtpRegistration", timeToLive = 300L )//5 minutes
public class OtpRegistration extends TokenBase {
}
