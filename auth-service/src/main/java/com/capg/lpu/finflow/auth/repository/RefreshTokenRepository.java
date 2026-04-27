package com.capg.lpu.finflow.auth.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capg.lpu.finflow.auth.entity.RefreshToken;
import com.capg.lpu.finflow.auth.entity.User;

/**
 * Repository for browser refresh-token sessions.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);

    long deleteByExpiresAtBefore(LocalDateTime timestamp);

    java.util.List<RefreshToken> findByUserAndRevokedFalse(User user);
}
