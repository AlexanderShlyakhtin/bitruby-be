package kg.bitruby.usersapp.outcomes.postgres.repository;

import kg.bitruby.usersapp.outcomes.postgres.domain.OtpRestorePasswordTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRestorePasswordTokenEntityRepository
    extends JpaRepository<OtpRestorePasswordTokenEntity, String> {
}
