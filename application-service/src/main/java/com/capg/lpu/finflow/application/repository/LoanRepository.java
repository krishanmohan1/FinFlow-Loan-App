package com.capg.lpu.finflow.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capg.lpu.finflow.application.entity.LoanApplication;

import java.util.List;

/**
 * Repository interface for LoanApplication entities.
 * Provides standard CRUD operations and custom query methods for database interaction.
 */
@Repository
public interface LoanRepository extends JpaRepository<LoanApplication, Long> {

    /**
     * Retrieves all loan applications submitted by a specific user.
     *
     * @param username The username of the applicant.
     * @return A list of loan applications associated with the given username.
     */
    List<LoanApplication> findByUsername(String username);

    /**
     * Retrieves all loan applications that match a specific status.
     *
     * @param status The target status for filtering (e.g., PENDING, APPROVED).
     * @return A list of loan applications with the specified status.
     */
    List<LoanApplication> findByStatus(String status);

    /**
     * Retrieves loan applications for a specific user filtered by their current status.
     *
     * @param username The username of the applicant.
     * @param status The specific status to filter by.
     * @return A filtered list of loan applications matching both criteria.
     */
    List<LoanApplication> findByUsernameAndStatus(String username, String status);
}