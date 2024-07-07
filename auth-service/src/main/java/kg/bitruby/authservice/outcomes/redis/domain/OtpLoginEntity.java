package kg.bitruby.authservice.outcomes.redis.domain;

import kg.bitruby.commonmodule.domain.TokenBase;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "OtpLogin", timeToLive = 300L ) //5 minutes
public class OtpLoginEntity extends TokenBase {
}
