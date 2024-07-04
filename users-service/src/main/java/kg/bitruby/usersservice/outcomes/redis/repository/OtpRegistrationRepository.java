package kg.bitruby.usersservice.outcomes.redis.repository;

import kg.bitruby.usersservice.outcomes.redis.domain.OtpRegistration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OtpRegistrationRepository extends CrudRepository<OtpRegistration, UUID> {
}
