package kg.bitruby.authservice.repository;

import kg.bitruby.authservice.entity.OtpLoginTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OtpTokenRepository extends JpaRepository<OtpLoginTokenEntity, String> {
}
