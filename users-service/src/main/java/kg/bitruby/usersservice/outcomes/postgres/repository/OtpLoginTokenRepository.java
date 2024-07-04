package kg.bitruby.usersservice.outcomes.postgres.repository;

import kg.bitruby.usersservice.outcomes.postgres.domain.OtpLoginTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpLoginTokenRepository
    extends JpaRepository<OtpLoginTokenEntity, String> {
  Optional<OtpLoginTokenEntity> findByTokenAndValid(String token, Boolean valid);
  Optional<OtpLoginTokenEntity> findByIdAndValid(String id, Boolean valid);
}
