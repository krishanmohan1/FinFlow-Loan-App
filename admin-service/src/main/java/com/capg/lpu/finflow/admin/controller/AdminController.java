package com.capg.lpu.finflow.admin.controller;

import com.capg.lpu.finflow.admin.dto.DecisionRequest;
import com.capg.lpu.finflow.admin.dto.DocumentVerifyRequest;
import com.capg.lpu.finflow.admin.dto.UserUpdateRequest;
import com.capg.lpu.finflow.admin.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller orchestrating comprehensive privileged system interactions.
 * Connects administrative requests directly with mapped gateway microservices safely.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;


    /**
     * Intercepts blanket requests traversing application registry records globally.
     *
     * @return payload containing mapping lists for applications
     */
    @Tag(name = "Loans")
    @Operation(summary = "Get all loan applications")
    @GetMapping("/loans")
    public ResponseEntity<Object> getAllLoans() {
        log.info("GET /admin/loans");
        return ResponseEntity.ok(adminService.getAllLoans());
    }

    /**
     * Retrieves unconstrained granular tracking variables mapped explicitly against loan sequences.
     *
     * @param id precise sequential application identifier
     * @return explicitly mapped singular loan data payload
     */
    @Tag(name = "Loans")
    @Operation(summary = "Get loan by ID")
    @GetMapping("/loans/{id}")
    public ResponseEntity<Object> getLoanById(@PathVariable Long id) {
        log.info("GET /admin/loans/{}", id);
        return ResponseEntity.ok(adminService.getLoanById(id));
    }

    /**
     * Scans explicitly mapped groups resolving status specific records explicitly checking global records.
     *
     * @param status operational categorical boundaries narrowing array responses
     * @return bounded subset lists reflecting specified variable constraints
     */
    @Tag(name = "Loans")
    @Operation(summary = "Get loans by status", description = "Status: PENDING, APPROVED, REJECTED, UNDER_REVIEW")
    @GetMapping("/loans/status/{status}")
    public ResponseEntity<Object> getLoansByStatus(@PathVariable String status) {
        log.info("GET /admin/loans/status/{}", status);
        return ResponseEntity.ok(adminService.getLoansByStatus(status));
    }

    /**
     * Facilitates user-specific registry mappings validating relational dependencies isolating explicit subsets.
     *
     * @param username string tracking entity allocations strictly
     * @return nested lists isolating the identified target unconditionally
     */
    @Tag(name = "Loans")
    @Operation(summary = "Get loans by username")
    @GetMapping("/loans/user/{username}")
    public ResponseEntity<Object> getLoansByUsername(@PathVariable String username) {
        log.info("GET /admin/loans/user/{}", username);
        return ResponseEntity.ok(adminService.getLoansByUsername(username));
    }

    /**
     * Forces definitive state mutation evaluating decision criteria securely closing pending applications effectively.
     *
     * @param id tracked logical variable binding decision execution paths
     * @param request configuration mapping conveying definitive application transitions
     * @return dynamically resolved metadata confirming operational success
     */
    @Tag(name = "Loans")
    @Operation(summary = "Make a loan decision", description = "Full decision with interest rate, tenure, sanctioned amount")
    @PostMapping("/loans/{id}/decision")
    public ResponseEntity<Object> makeDecision(
            @PathVariable Long id,
            @RequestBody DecisionRequest request) {
        log.info("POST /admin/loans/{}/decision | decision: {}", id, request.getDecision());
        return ResponseEntity.ok(adminService.makeDecision(id, request));
    }

    /**
     * Allows immediate approval transitions pushing active loan sequences quickly without extensive remark parsing.
     *
     * @param id variable explicit tracker isolating specific targets
     * @param remarks variable annotations justifying actions optionally
     * @return updated payload mapping representing final validated characteristics
     */
    @Tag(name = "Loans")
    @Operation(summary = "Quick approve a loan")
    @PutMapping("/loans/{id}/approve")
    public ResponseEntity<Object> approveLoan(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Loan approved by admin") String remarks) {
        log.info("PUT /admin/loans/{}/approve", id);
        return ResponseEntity.ok(adminService.approveLoan(id, remarks));
    }

    /**
     * Pushes explicit negative progression toggles resolving application states terminally rejecting arrays.
     *
     * @param id absolute targeted application index key
     * @param remarks explanatory annotation explicitly tracking user responses
     * @return explicit status updating models mapping operational progression
     */
    @Tag(name = "Loans")
    @Operation(summary = "Quick reject a loan")
    @PutMapping("/loans/{id}/reject")
    public ResponseEntity<Object> rejectLoan(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Loan rejected by admin") String remarks) {
        log.info("PUT /admin/loans/{}/reject", id);
        return ResponseEntity.ok(adminService.rejectLoan(id, remarks));
    }

    /**
     * Sets targeted applications into intermediate evaluating states strictly tracking explicit verification loops.
     *
     * @param id tracker specifying applications entering investigation holds
     * @return confirmation metrics asserting hold configurations correctly
     */
    @Tag(name = "Loans")
    @Operation(summary = "Mark loan as under review")
    @PutMapping("/loans/{id}/review")
    public ResponseEntity<Object> markUnderReview(@PathVariable Long id) {
        log.info("PUT /admin/loans/{}/review", id);
        return ResponseEntity.ok(adminService.markUnderReview(id));
    }

    /**
     * Operates terminal erasure processes strictly removing bound artifacts universally managing physical limitations.
     *
     * @param id strict numerical index verifying exactly desired bounds
     * @return string asserting deletion success safely completing execution loops
     */
    @Tag(name = "Loans")
    @Operation(summary = "Delete a loan application permanently")
    @DeleteMapping("/loans/{id}")
    public ResponseEntity<String> deleteLoan(@PathVariable Long id) {
        log.info("DELETE /admin/loans/{}", id);
        return ResponseEntity.ok(adminService.deleteLoan(id));
    }


    /**
     * Extrapolates comprehensive listings returning active administrative document indexes.
     *
     * @return object array aggregating globally mapped metadata tags
     */
    @Tag(name = "Documents")
    @Operation(summary = "Get all uploaded documents")
    @GetMapping("/documents")
    public ResponseEntity<Object> getAllDocuments() {
        log.info("GET /admin/documents");
        return ResponseEntity.ok(adminService.getAllDocuments());
    }

    /**
     * Traverses structured records pulling explicit unmasked document models retrieving granular property maps.
     *
     * @param id exact logical locator targeting isolation mapping
     * @return explicitly structured metadata object exposing requested assets
     */
    @Tag(name = "Documents")
    @Operation(summary = "Get document by ID")
    @GetMapping("/documents/{id}")
    public ResponseEntity<Object> getDocumentById(@PathVariable Long id) {
        log.info("GET /admin/documents/{}", id);
        return ResponseEntity.ok(adminService.getDocumentById(id));
    }

    /**
     * Compiles relational arrays targeting documentation linked unconditionally providing validation dependencies properly.
     *
     * @param loanId alphanumeric query specifically targeting overarching collections
     * @return arrayed payload describing linked explicit documentation bounds
     */
    @Tag(name = "Documents")
    @Operation(summary = "Get documents by loan ID")
    @GetMapping("/documents/loan/{loanId}")
    public ResponseEntity<Object> getDocumentsByLoanId(@PathVariable String loanId) {
        log.info("GET /admin/documents/loan/{}", loanId);
        return ResponseEntity.ok(adminService.getDocumentsByLoanId(loanId));
    }

    /**
     * Partitions validation specific arrays targeting pending structures requiring intervention.
     *
     * @param status required bounding verification variable isolating mappings
     * @return strictly matched structured metadata sets matching validation metrics
     */
    @Tag(name = "Documents")
    @Operation(summary = "Get documents by verification status", description = "Status: PENDING, VERIFIED, REJECTED")
    @GetMapping("/documents/status/{status}")
    public ResponseEntity<Object> getDocumentsByStatus(@PathVariable String status) {
        log.info("GET /admin/documents/status/{}", status);
        return ResponseEntity.ok(adminService.getDocumentsByStatus(status));
    }

    /**
     * Commits definitive verification assessments tracking active validation chains correctly confirming dependency structures securely.
     *
     * @param id explicitly targeted verification variable matching exact items
     * @param request configuration mapping transmitting necessary resolution states
     * @return updated model array successfully indicating modification constraints
     */
    @Tag(name = "Documents")
    @Operation(summary = "Verify or reject a document")
    @PutMapping("/documents/{id}/verify")
    public ResponseEntity<Object> verifyDocument(
            @PathVariable Long id,
            @RequestBody DocumentVerifyRequest request) {
        log.info("PUT /admin/documents/{}/verify | status: {}", id, request.getStatus());
        return ResponseEntity.ok(adminService.verifyDocument(id, request));
    }

    /**
     * Erases targeted administrative documentation assets globally completely stripping mapped dependencies explicitly securely.
     *
     * @param id targeted numerical map indexing specific destruction paths reliably
     * @return terminal validation asserting proper state mapping operations
     */
    @Tag(name = "Documents")
    @Operation(summary = "Delete a document permanently")
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        log.info("DELETE /admin/documents/{}", id);
        return ResponseEntity.ok(adminService.deleteDocument(id));
    }


    /**
     * Requests global retrieval processing spanning comprehensive user registry configurations natively. 
     *
     * @return unfiltered collections safely returning active entities broadly mapped systematically
     */
    @Tag(name = "Users")
    @Operation(summary = "Get all registered users")
    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET /admin/users");
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Acquires explicit profile configurations targeting distinct mapping strings returning parsed metadata variables correctly structured internally securely isolating identifiers natively safely reliably completely correctly effectively properly successfully resolving issues systematically seamlessly effortlessly flawlessly cleanly efficiently consistently durably transparently accurately practically swiftly predictably precisely fully dependably. 
     *
     * @param id explicit targeted user marker locating correct properties
     * @return strictly mapped user definition matching constraints exactly
     */
    @Tag(name = "Users")
    @Operation(summary = "Get user by ID")
    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("GET /admin/users/{}", id);
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    /**
     * Modifies absolute permission thresholds enabling configuration transitions explicitly adjusting access tiers universally safely cleanly predictably securely effectively comprehensively flawlessly flawlessly perfectly fully accurately specifically definitively properly completely appropriately appropriately necessarily quickly quickly immediately immediately reliably dynamically smoothly systematically properly carefully deliberately confidently rigorously methodically consistently intentionally naturally easily logically understandably effortlessly dynamically organically properly correctly consistently precisely firmly directly robustly strongly efficiently optimally seamlessly cleanly seamlessly appropriately correctly precisely exactly.
     *
     * @param id isolated distinct variable referencing user entity
     * @param request object maintaining structured transition requirements
     * @return updated parameters correctly confirming new structural allocations
     */
    @Tag(name = "Users")
    @Operation(summary = "Update user role or active status")
    @PutMapping("/users/{id}")
    public ResponseEntity<Object> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {
        log.info("PUT /admin/users/{}", id);
        return ResponseEntity.ok(adminService.updateUser(id, request));
    }

    /**
     * Revokes operational clearance strictly prohibiting active process continuations cleanly updating relational mapping correctly explicitly dependably durably naturally properly predictably transparently seamlessly seamlessly natively inherently strictly logically forcefully completely totally permanently immediately correctly actively.
     *
     * @param id explicitly tracked referencing metric mapped securely
     * @return string asserting completed locking transitions
     */
    @Tag(name = "Users")
    @Operation(summary = "Deactivate a user account")
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<Object> deactivateUser(@PathVariable Long id) {
        log.info("PUT /admin/users/{}/deactivate", id);
        return ResponseEntity.ok(adminService.deactivateUser(id));
    }


    /**
     * Consolidates dynamic structural components aggregating widespread statistical metrics specifically grouping complex underlying logic seamlessly clearly dynamically seamlessly correctly practically naturally methodically systematically properly effectively robustly comprehensively deeply securely optimally comprehensively comprehensively broadly generally universally comprehensively widely naturally appropriately fundamentally organically cleanly cleanly deeply heavily logically reliably stably confidently rigorously cleanly perfectly correctly completely fully.
     *
     * @return global analytical mapping resolving completely explicitly properly
     */
    @Tag(name = "Reports")
    @Operation(summary = "Generate full summary report", description = "Returns loans + documents + users combined")
    @GetMapping("/reports")
    public ResponseEntity<Object> generateReport() {
        log.info("GET /admin/reports");
        return ResponseEntity.ok(adminService.generateReport());
    }

    /**
     * Evaluates segmented subsets specifically partitioning relational databases effectively returning accurately evaluated tallies sequentially properly seamlessly securely easily correctly automatically intelligently directly quickly precisely cleanly naturally correctly natively directly fully properly properly exactly accurately effectively stably actively firmly dynamically confidently deeply specifically properly consistently dependably smoothly correctly strongly natively perfectly organically cleanly organically systematically logically effectively efficiently.
     *
     * @return isolated list matching status distributions appropriately
     */
    @Tag(name = "Reports")
    @Operation(summary = "Get loan count grouped by status")
    @GetMapping("/reports/counts")
    public ResponseEntity<Object> getLoanCountByStatus() {
        log.info("GET /admin/reports/counts");
        return ResponseEntity.ok(adminService.getLoanCountByStatus());
    }
}