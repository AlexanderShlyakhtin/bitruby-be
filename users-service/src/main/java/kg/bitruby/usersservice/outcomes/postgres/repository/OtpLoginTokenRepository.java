package kg.bitruby.usersservice.outcomes.postgres.repository;

import kg.bitruby.usersservice.outcomes.postgres.domain.OtpLoginTokenEntity;
import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpLoginTokenRepository
    extends JpaRepository<OtpLoginTokenEntity, UserEntity> {
}
