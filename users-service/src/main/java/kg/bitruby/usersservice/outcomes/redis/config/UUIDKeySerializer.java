package kg.bitruby.usersservice.outcomes.redis.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDKeySerializer implements RedisSerializer<UUID> {

  @Override
  public byte[] serialize(UUID uuid) throws SerializationException {
    Assert.notNull(uuid, "UUID must not be null");
    return ByteBuffer.wrap(new byte[16])
        .putLong(uuid.getMostSignificantBits())
        .putLong(uuid.getLeastSignificantBits())
        .array();
  }

  @Override
  public UUID deserialize(byte[] bytes) throws SerializationException {
    Assert.notNull(bytes, "Serialized bytes must not be null");
    Assert.isTrue(bytes.length == 16, "Serialized byte array must be 16 bytes long");
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    long mostSignificantBits = byteBuffer.getLong();
    long leastSignificantBits = byteBuffer.getLong();
    return new UUID(mostSignificantBits, leastSignificantBits);
  }
}

