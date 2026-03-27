package com.capg.lpu.finflow.document.service;

import com.capg.lpu.finflow.document.entity.Document;
import com.capg.lpu.finflow.document.exception.ResourceNotFoundException;
import com.capg.lpu.finflow.document.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for DocumentService.
 * Validates document upload constraints, retrieval, and administrative verification workflows.
 */
@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private DocumentService documentService;

    private Document sampleDocument;

    /**
     * Sets up a common document entity for testing purposes.
     */
    @BeforeEach
    void setUp() {
        sampleDocument = new Document();
        sampleDocument.setId(50L);
        sampleDocument.setUsername("testuser");
        sampleDocument.setLoanId("LOAN-101");
        sampleDocument.setDocumentType("PASSPORT");
        sampleDocument.setFileName("1234_passport.jpg");
        sampleDocument.setFilePath("/tmp/dummy_path/1234_passport.jpg");
        sampleDocument.setVerificationStatus("PENDING");
    }

    /**
     * Verifies that the service prevents multiple uploads of the same document type for a single loan application.
     */
    @Test
    @DisplayName("test upload() - Should prevent duplicate document types for same loan")
    void testUpload_DuplicateDocument() {
        // Arrange
        when(documentRepository.findByLoanIdAndDocumentType("LOAN-101", "PASSPORT"))
                .thenReturn(Optional.of(sampleDocument));

        // Act & Assert
        assertThatThrownBy(() -> 
                documentService.upload(mockFile, "LOAN-101", "PASSPORT", "testuser"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("already uploaded for loan ID");
            
        verify(documentRepository, never()).save(any());
    }

    /**
     * Confirms that an existing document can be retrieved successfully using its unique ID.
     */
    @Test
    @DisplayName("test getById() - Should return document if exists")
    void testGetById_Success() {
        // Arrange
        when(documentRepository.findById(50L)).thenReturn(Optional.of(sampleDocument));

        // Act
        Document result = documentService.getById(50L);

        // Assert
        assertThat(result.getId()).isEqualTo(50L);
        assertThat(result.getDocumentType()).isEqualTo("PASSPORT");
    }

    /**
     * Verifies that the service throws a ResourceNotFoundException when requesting a non-existent document ID.
     */
    @Test
    @DisplayName("test getById() - Should throw ResourceNotFoundException if not exists")
    void testGetById_NotFound() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.getById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Document not found");
    }

    /**
     * Validates that an administrator can correctly update the verification status and remarks for a document.
     */
    @Test
    @DisplayName("test verifyDocument() - Should successfully change status to VERIFIED")
    void testVerifyDocument_Success() {
        // Arrange
        when(documentRepository.findById(50L)).thenReturn(Optional.of(sampleDocument));
        
        Document updatedMock = new Document();
        updatedMock.setId(50L);
        updatedMock.setVerificationStatus("VERIFIED");
        updatedMock.setVerifiedRemarks("Looks perfectly authentic.");

        when(documentRepository.save(any(Document.class))).thenReturn(updatedMock);

        // Act
        Document result = documentService.verifyDocument(50L, "VERIFIED", "Looks perfectly authentic.");

        // Assert
        assertThat(result.getVerificationStatus()).isEqualTo("VERIFIED");
        assertThat(result.getVerifiedRemarks()).isEqualTo("Looks perfectly authentic.");
        
        verify(documentRepository, times(1)).save(sampleDocument);
    }
    
    /**
     * Ensures that invalid statuses are rejected during the document verification process.
     */
    @Test
    @DisplayName("test verifyDocument() - Should block invalid statuses")
    void testVerifyDocument_InvalidStatus() {
        // Act & Assert
        assertThatThrownBy(() -> documentService.verifyDocument(50L, "FAKE_STATUS", "Random stuff"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Must be VERIFIED or REJECTED");
            
        verify(documentRepository, never()).save(any());
    }
}
