package com.bitruby.usersapp.outcomes.postgres.repository;

import com.bitruby.usersapp.outcomes.postgres.entity.OtpRegistrationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRegistrationTokenEntityRepository
    extends JpaRepository<OtpRegistrationTokenEntity, String> {
}
