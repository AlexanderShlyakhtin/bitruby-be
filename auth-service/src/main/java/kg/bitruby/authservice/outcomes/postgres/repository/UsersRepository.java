package kg.bitruby.authservice.outcomes.postgres.repository;

import kg.bitruby.authservice.outcomes.postgres.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<UserEntity, UUID> {

  List<UserEntity> findByEmailAndIsEnabledTrue(String email);
  List<UserEntity> findByPhoneAndIsEnabledTrue(String phone);

}
