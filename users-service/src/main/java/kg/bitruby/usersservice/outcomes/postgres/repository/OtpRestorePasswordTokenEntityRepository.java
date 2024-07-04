package kg.bitruby.usersservice.outcomes.postgres.repository;

import kg.bitruby.usersservice.outcomes.postgres.domain.OtpRestorePasswordTokenEntity;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRestorePasswordTokenEntityRepository
    extends JpaRepository<OtpRestorePasswordTokenEntity, UserEntity> {
  Optional<OtpRestorePasswordTokenEntity> findByToken(String token);
}
