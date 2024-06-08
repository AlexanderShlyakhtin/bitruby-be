package com.bitruby.usersapp.outcomes.postgres.repository;

import com.bitruby.usersapp.outcomes.postgres.entity.OtpLoginTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTokenRepository
    extends JpaRepository<OtpLoginTokenEntity, String> {
  Optional<OtpLoginTokenEntity> findByTokenAndValid(String token, Boolean valid);
  Optional<OtpLoginTokenEntity> findByIdAndValid(String id, Boolean valid);
}
