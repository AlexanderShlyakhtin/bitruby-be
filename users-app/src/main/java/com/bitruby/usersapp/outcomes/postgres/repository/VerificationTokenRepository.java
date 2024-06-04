package com.bitruby.usersapp.outcomes.postgres.repository;

import com.bitruby.usersapp.outcomes.postgres.entity.VerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository
    extends JpaRepository<VerificationTokenEntity, UUID> {
  Optional<VerificationTokenEntity> findByTokenAndValid(UUID token, Boolean valid);
  Optional<VerificationTokenEntity> findByIdAndValid(UUID id, Boolean valid);
}
