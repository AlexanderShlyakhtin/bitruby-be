package com.bitruby.usersapp.outcomes.postgres.repository;

import com.bitruby.usersapp.outcomes.postgres.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByPhone(String phone);
  Optional<UserEntity> findByEmail(String email);
}
