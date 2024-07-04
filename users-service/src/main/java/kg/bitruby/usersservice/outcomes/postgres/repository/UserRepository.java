package kg.bitruby.usersservice.outcomes.postgres.repository;

import kg.bitruby.usersservice.outcomes.postgres.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  List<UserEntity> findByEmailAndIsEnabledTrue(String email);
  List<UserEntity> findByPhoneAndIsEnabledTrue(String phone);
  Optional<UserEntity> findByPhone(String phone);
  Optional<UserEntity> findByEmail(String email);
}
