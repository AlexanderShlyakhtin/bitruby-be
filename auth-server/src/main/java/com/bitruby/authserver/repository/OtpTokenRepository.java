package com.bitruby.authserver.repository;

import com.bitruby.authserver.entity.OtpTokenLoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OtpTokenRepository extends JpaRepository<OtpTokenLoginEntity, String> {
}
