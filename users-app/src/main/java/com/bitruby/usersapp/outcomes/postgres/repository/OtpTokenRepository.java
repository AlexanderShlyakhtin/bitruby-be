package com.bitruby.usersapp.outcomes.postgres.repository;

import com.bitruby.usersapp.outcomes.postgres.entity.OtpTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpTokenRepository
    extends JpaRepository<OtpTokenEntity, String> {
  Optional<OtpTokenEntity> findByTokenAndValid(String token, Boolean valid);
  Optional<OtpTokenEntity> findByIdAndValid(String id, Boolean valid);
}
