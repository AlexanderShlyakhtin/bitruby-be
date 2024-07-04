package kg.bitruby.authservice.repository;

import kg.bitruby.authservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  Optional<UserEntity> findByEmail(String email);
  Optional<UserEntity> findByPhone(String phoneNumber);
}
