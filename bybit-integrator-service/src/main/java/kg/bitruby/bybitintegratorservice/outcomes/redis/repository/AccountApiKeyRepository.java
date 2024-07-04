package kg.bitruby.bybitintegratorservice.outcomes.redis.repository;

import kg.bitruby.bybitintegratorservice.outcomes.redis.domain.AccountApiKeyEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountApiKeyRepository extends CrudRepository<AccountApiKeyEntity, UUID> {
}
