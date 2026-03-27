package com.capg.lpu.finflow.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.capg.lpu.finflow.auth.entity.User;

import java.util.Optional;

/**
 * Repository interface facilitating interactions between the application
 * and the underlying relational database for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Attempts to retrieve a unique user strictly matching the provided username.
     *
     * @param username the designated user identifier
     * @return an Optional potentially containing the matched user entity
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks for user existence dynamically using an Oracle SQL compatible JPQL validation query 
     * instead of relying natively on Spring Data's existsBy method.
     *
     * @param username the username being queried
     * @return true if the database yields any row matching the criteria, false otherwise
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);
}