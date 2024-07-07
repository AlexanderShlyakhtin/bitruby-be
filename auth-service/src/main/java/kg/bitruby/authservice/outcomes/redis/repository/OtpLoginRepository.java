package kg.bitruby.authservice.outcomes.redis.repository;

import kg.bitruby.authservice.outcomes.redis.domain.OtpLoginEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpLoginRepository extends CrudRepository<OtpLoginEntity, String> {
  Optional<OtpLoginEntity> findByUserId(UUID userId);
}
