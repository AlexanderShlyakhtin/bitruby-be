package kg.bitruby.bybitintegrator.outcomes.redis.repository;

import kg.bitruby.bybitintegrator.outcomes.redis.domain.AccountApiKeyEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountApiKeyRepository extends CrudRepository<AccountApiKeyEntity, UUID> {
}
