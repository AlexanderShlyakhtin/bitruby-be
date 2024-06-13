package kg.bitruby.usersapp.outcomes.postgres.repository;

import kg.bitruby.usersapp.outcomes.postgres.domain.OtpRegistrationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRegistrationTokenRepository
    extends JpaRepository<OtpRegistrationTokenEntity, String> {
}
