package com.finflow.document.repository;

import com.finflow.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUsername(String username);

    List<Document> findByLoanId(String loanId);

    List<Document> findByEventType(String eventType);
}