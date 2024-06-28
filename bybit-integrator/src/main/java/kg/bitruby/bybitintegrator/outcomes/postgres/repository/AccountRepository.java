package kg.bitruby.bybitintegrator.outcomes.postgres.repository;

import kg.bitruby.bybitintegrator.outcomes.postgres.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
}
