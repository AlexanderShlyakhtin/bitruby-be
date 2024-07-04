package kg.bitruby.authservice.outcomes.postgres.repository;

import kg.bitruby.authservice.outcomes.postgres.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByEmailAndIsEnabledTrue(String email);
  Optional<UserEntity> findByPhoneAndIsEnabledTrue(String email);

}
