package com.capg.lpu.finflow.document.controller;

import com.capg.lpu.finflow.document.entity.Document;
import com.capg.lpu.finflow.document.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    private Document document;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        document = Document.builder()
                .id(1L)
                .username("user1")
                .loanId("1")
                .documentType("AADHAAR")
                .fileName("aadhaar.pdf")
                .filePath("/tmp/aadhaar.pdf")
                .verificationStatus("PENDING")
                .build();
        file = new MockMultipartFile("file", "aadhaar.pdf", "application/pdf", "demo".getBytes());
    }

    @Test
    void uploadAndReadEndpointsDelegateProperly() {
        when(documentService.upload(file, "1", "AADHAAR", "user1")).thenReturn(document);
        when(documentService.getAll()).thenReturn(List.of(document));
        when(documentService.getById(1L)).thenReturn(document);
        when(documentService.getByLoanId("1")).thenReturn(List.of(document));
        when(documentService.getByUsernameAndLoanId("user1", "1")).thenReturn(List.of(document));
        when(documentService.getByUsername("user1")).thenReturn(List.of(document));
        when(documentService.getByVerificationStatus("PENDING")).thenReturn(List.of(document));

        assertThat(documentController.upload(file, "1", "AADHAAR", "user1").getBody()).isEqualTo(document);
        assertThat(documentController.getAll("ADMIN").getBody()).containsExactly(document);
        assertThat(documentController.getById(1L, "user1", "USER").getBody()).isEqualTo(document);
        assertThat(documentController.getByLoan("1", "admin", "ADMIN").getBody()).containsExactly(document);
        assertThat(documentController.getByLoan("1", "user1", "USER").getBody()).containsExactly(document);
        assertThat(documentController.getMyDocuments("user1").getBody()).containsExactly(document);
        assertThat(documentController.getByStatus("PENDING", "ADMIN").getBody()).containsExactly(document);
    }

    @Test
    void mutatingEndpointsEnforceOwnershipAndAdminRules() {
        DocumentController.VerifyRequest verifyRequest = new DocumentController.VerifyRequest();
        verifyRequest.setStatus("VERIFIED");
        verifyRequest.setRemarks("looks good");

        when(documentService.getById(1L)).thenReturn(document);
        when(documentService.updateFile(1L, file)).thenReturn(document);
        when(documentService.verifyDocument(1L, "VERIFIED", "looks good")).thenReturn(document);
        when(documentService.deleteDocument(1L)).thenReturn("deleted");

        assertThat(documentController.update(1L, file, "user1", "USER").getBody()).isEqualTo(document);
        assertThat(documentController.verify(1L, verifyRequest, "ADMIN").getBody()).isEqualTo(document);
        assertThat(documentController.delete(1L, "ADMIN").getBody()).isEqualTo("deleted");

        assertThatThrownBy(() -> documentController.update(1L, file, "other", "USER"))
                .isInstanceOf(SecurityException.class);
        assertThatThrownBy(() -> documentController.getAll("USER"))
                .isInstanceOf(SecurityException.class);
        assertThatThrownBy(() -> documentController.getById(1L, "other", "USER"))
                .isInstanceOf(SecurityException.class);
        assertThatThrownBy(() -> documentController.getByStatus("PENDING", "USER"))
                .isInstanceOf(SecurityException.class);
        assertThatThrownBy(() -> documentController.verify(1L, verifyRequest, "USER"))
                .isInstanceOf(SecurityException.class);
        assertThatThrownBy(() -> documentController.delete(1L, "USER"))
                .isInstanceOf(SecurityException.class);

        verify(documentService).deleteDocument(1L);
    }
}
