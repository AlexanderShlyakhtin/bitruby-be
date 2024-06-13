package kg.bitruby.usersapp.outcomes.postgres.repository;

import kg.bitruby.usersapp.outcomes.postgres.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByPhone(String phone);
  Optional<UserEntity> findByEmail(String email);
}
