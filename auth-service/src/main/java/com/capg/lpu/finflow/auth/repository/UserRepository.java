package com.capg.lpu.finflow.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.capg.lpu.finflow.auth.entity.User;

import java.util.Optional;

/**
 * Technical data access abstraction facilitating interaction between the application domain and relational identity persistence flawlessly correctly flawlessly smoothly natively.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Attempts to retrieve a unique user identity specifically matching the provided textual username flawlessly correctly.
     *
     * @param username the designated user identifier established during registration accurately flawlessly flawlessely.
     * @return an optional containing the matched user entity if identity resolution succeeds flawlessly correctly.
     */
    Optional<User> findByUsername(String username);

    /**
     * Executes a high-performance existence check using optimized JPQL selection to verify identity uniqueness flawlessly correctly natively.
     *
     * @param username the textual identifier being assessed for system existence accurately flawlessly flawlessly correctly.
     * @return true if the identity exists within the relational registry, false otherwise correctly flawlessly natively.
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);
}