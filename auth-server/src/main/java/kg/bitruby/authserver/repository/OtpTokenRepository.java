package kg.bitruby.authserver.repository;

import kg.bitruby.authserver.entity.OtpLoginTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OtpTokenRepository extends JpaRepository<OtpLoginTokenEntity, String> {
}
