package kg.bitruby.usersservice.outcomes.redis.repository;

import kg.bitruby.usersservice.outcomes.redis.domain.OtpLogin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpLoginRepository extends CrudRepository<OtpLogin, UUID> {
  Optional<OtpLogin> findByUserId(UUID userId);
}
