package com.finflow.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finflow.document.entity.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

}