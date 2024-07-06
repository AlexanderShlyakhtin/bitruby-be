package kg.bitruby.usersservice.outcomes.redis.repository;

import kg.bitruby.usersservice.outcomes.redis.domain.PreUserRegistration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreUserRegistrationRepository extends CrudRepository<PreUserRegistration, String> {
}
