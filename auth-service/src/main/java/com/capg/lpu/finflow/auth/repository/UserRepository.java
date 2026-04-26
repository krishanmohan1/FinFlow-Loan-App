package com.capg.lpu.finflow.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.capg.lpu.finflow.auth.entity.User;

import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Handles database interactions for user profile and identity management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique username.
     *
     * @param username The username to search for.
     * @return An Optional containing the User if found, or empty otherwise.
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user already exists with the given username.
     * Use this for validating username uniqueness during registration.
     *
     * @param username The username to check for existence.
     * @return true if a user with the specified username exists.
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    /**
     * Checks if a user already exists with the given email address.
     *
     * @param email The email address to check.
     * @return true if a user with the specified email exists.
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE lower(u.email) = lower(:email)")
    boolean existsByEmail(@Param("email") String email);
}
