package kg.bitruby.usersapp.outcomes.postgres.repository;

import kg.bitruby.usersapp.outcomes.postgres.domain.UsersVerificationSessions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsersVerificationSessionsRepository
    extends JpaRepository<UsersVerificationSessions, UUID> {
  Optional<UsersVerificationSessions> findByIdAndActiveTrue(UUID id);
  Optional<UsersVerificationSessions> findByUserId_IdAndActiveTrue(UUID id);
}
