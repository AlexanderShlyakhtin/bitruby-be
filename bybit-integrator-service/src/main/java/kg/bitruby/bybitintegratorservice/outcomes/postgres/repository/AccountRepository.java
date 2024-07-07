package kg.bitruby.bybitintegratorservice.outcomes.postgres.repository;

import kg.bitruby.bybitintegratorservice.outcomes.postgres.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
  Optional<AccountEntity> findByUserId(UUID userId);
  Optional<AccountEntity> findByUserIdAndIsActiveTrue(UUID userId);
}
