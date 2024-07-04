package kg.bitruby.authservice.repository;

import kg.bitruby.authservice.entity.OtpLoginTokenEntity;
import kg.bitruby.authservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OtpTokenRepository extends JpaRepository<OtpLoginTokenEntity, UserEntity> {
}
