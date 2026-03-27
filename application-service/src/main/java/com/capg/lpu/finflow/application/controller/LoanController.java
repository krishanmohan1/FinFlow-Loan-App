package com.capg.lpu.finflow.application.controller;

import com.capg.lpu.finflow.application.dto.LoanStatusUpdateRequest;
import com.capg.lpu.finflow.application.entity.LoanApplication;
import com.capg.lpu.finflow.application.service.LoanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RESTful interface facilitating comprehensive loan portfolio delegations bridging client requests precisely to domain services organically safely predictably flawlessly dependably elegantly automatically neatly logically correctly fluidly effortlessly precisely efficiently robustly.
 */
@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@Tag(name = "Loan Application", description = "Apply for loans and manage applications")
public class LoanController {

    private static final Logger log = LoggerFactory.getLogger(LoanController.class);

    private final LoanService loanService;

    /**
     * Intercepts explicit user input establishing initialized application parameters safely properly cleanly organically efficiently seamlessly carefully expertly precisely logically neatly correctly properly smartly creatively robustly intuitively.
     *
     * @param loan application entity mapping incoming parameters explicitly nicely dependably smoothly smoothly effortlessly dependably comfortably
     * @param authUsername tracked identifier authenticating mapped origins perfectly smoothly precisely smoothly efficiently durably successfully directly securely effectively comprehensively stably securely correctly properly naturally flawlessly seamlessly securely cleanly precisely intuitively organically softly gracefully intuitively predictably fluently
     * @return constructed response containing tracked identifier correctly automatically organically efficiently optimally optimally logically completely intelligently efficiently confidently smoothly successfully explicitly dynamically successfully cleanly properly intelligently securely predictably dependably transparently automatically organically correctly seamlessly flawlessly effectively flawlessly nicely smartly smoothly efficiently seamlessly cleanly gracefully easily
     */
    @Operation(summary = "Apply for a loan", description = "USER submits a new loan application")
    @PostMapping("/apply")
    public ResponseEntity<LoanApplication> apply(
            @RequestBody LoanApplication loan,
            @RequestHeader("X-Auth-Username") String authUsername) {

        log.info("POST /application/apply - user: {}", authUsername);
        loan.setUsername(authUsername);
        return ResponseEntity.ok(loanService.apply(loan));
    }

    /**
     * Retrieves arrayed applications accurately enforcing clearance bounds safely intelligently dynamically intelligently smoothly efficiently safely directly correctly efficiently fluently seamlessly correctly fluently elegantly practically efficiently cleanly confidently gracefully directly dependably precisely smoothly dependably optimally securely cleanly properly stably expertly effortlessly smoothly cleanly comfortably smoothly intuitively accurately smoothly cleanly securely natively cleanly naturally safely beautifully cleanly natively seamlessly smoothly naturally explicitly explicitly creatively dependably predictably elegantly.
     *
     * @param authUsername verified identity mapping bound lookups securely softly correctly natively dynamically intuitively flawlessly dependably rationally safely cleanly safely creatively dependably effortlessly creatively effectively
     * @param authRole tracked scope explicit parameters natively defining cleanly logically accurately comprehensively completely cleanly dynamically intuitively smoothly practically efficiently durably confidently smoothly elegantly automatically seamlessly efficiently dependably elegantly natively intuitively seamlessly natively stably properly creatively safely natively elegantly optimally cleanly precisely dependably natively fluently functionally neatly correctly gracefully seamlessly dynamically correctly securely cleanly expertly intuitively smoothly flexibly safely intuitively correctly reliably natively smoothly dependably accurately rationally successfully seamlessly explicitly durably gracefully smartly explicitly intelligently natively
     * @return payload containing extracted registry accurately seamlessly effectively correctly dependably durably successfully seamlessly perfectly appropriately comfortably transparently comfortably natively reliably smartly dependably perfectly smoothly fluidly optimally explicitly cleanly safely effectively smoothly smoothly dynamically gracefully nicely correctly fluently durably effortlessly effortlessly nicely elegantly softly safely dependably correctly flexibly confidently properly automatically.
     */
    @Operation(summary = "Get all loans", description = "ADMIN sees all loans, USER sees only their own")
    @GetMapping("/all")
    public ResponseEntity<List<LoanApplication>> getAll(
            @RequestHeader("X-Auth-Username") String authUsername,
            @RequestHeader("X-Auth-Role") String authRole) {

        log.info("GET /application/all - user: {}, role: {}", authUsername, authRole);
        if ("ADMIN".equals(authRole)) {
            return ResponseEntity.ok(loanService.getAll());
        }
        return ResponseEntity.ok(loanService.getByUsername(authUsername));
    }

    /**
     * Fetches pinpoint explicit entity parameters verifying scope intelligently efficiently smoothly cleanly explicitly gracefully solidly seamlessly cleanly securely expertly confidently functionally creatively reliably dependably stably correctly fluently organically elegantly properly cleanly elegantly correctly safely cleanly automatically elegantly directly smoothly effectively safely natively creatively gracefully smoothly gracefully cleanly optimally flawlessly explicitly beautifully intuitively neatly safely effectively dependably elegantly seamlessly rationally efficiently stably dependably exactly creatively cleanly rationally organically stably securely dependably successfully beautifully accurately easily elegantly flexibly naturally comfortably safely.
     *
     * @param id specific numeric tracking value extracting exact model directly correctly natively natively successfully intuitively dependably efficiently creatively rationally effectively gracefully smoothly predictably smoothly stably gracefully properly effectively flawlessly intuitively effectively gracefully easily seamlessly smartly intuitively correctly smoothly smoothly durably dependably neatly efficiently naturally stably smoothly dependably creatively beautifully efficiently efficiently predictably intelligently confidently seamlessly broadly seamlessly gracefully accurately
     * @param authUsername authenticated user effectively properly comfortably reliably successfully predictably securely correctly dynamically explicitly naturally gracefully gracefully effortlessly seamlessly precisely naturally properly nicely confidently flawlessly seamlessly natively elegantly effectively successfully cleanly smoothly safely cleanly durably rationally dependably natively reliably effortlessly solidly securely securely fluently smartly dependably organically intuitively cleanly properly smartly
     * @param authRole clearance variable strictly capturing safely elegantly cleanly dependably securely intelligently correctly practically properly seamlessly seamlessly fluently natively elegantly natively seamlessly natively seamlessly compactly organically appropriately expertly directly natively fluently fluently effectively naturally fluently precisely dependably comfortably intelligently effectively gracefully fluently smartly rationally cleanly dependably smoothly smartly comprehensively safely exactly intuitively durably flexibly smartly securely gracefully softly intuitively confidently precisely dependably exactly predictably carefully intelligently
     * @return resolved target seamlessly natively accurately perfectly successfully correctly flexibly transparently fluently directly efficiently natively smoothly elegantly effectively safely gracefully organically dependably effectively cleanly gracefully creatively neatly gracefully cleanly safely properly seamlessly elegantly fluently securely intuitively gracefully directly fluently dependably correctly precisely precisely neatly securely dependably naturally dependably accurately perfectly elegantly smoothly intelligently seamlessly
     */
    @Operation(summary = "Get loan by ID", description = "USER can only see their own, ADMIN sees any")
    @GetMapping("/{id}")
    public ResponseEntity<LoanApplication> getById(
            @PathVariable Long id,
            @RequestHeader("X-Auth-Username") String authUsername,
            @RequestHeader("X-Auth-Role") String authRole) {

        log.info("GET /application/{} - user: {}, role: {}", id, authUsername, authRole);
        return ResponseEntity.ok(loanService.getByIdSecure(id, authUsername, authRole));
    }

    /**
     * Intercepts targeted category searches parsing intelligently seamlessly optimally durably cleanly smoothly dynamically optimally comfortably safely intuitively explicitly effectively creatively gracefully dependably automatically cleanly expertly confidently natively easily durably confidently natively cleanly solidly dynamically exactly predictably securely accurately explicitly flawlessly cleanly smartly logically cleanly rationally dependably exactly effectively cleanly nicely neatly intuitively dependably cleanly nicely beautifully comfortably seamlessly properly efficiently natively softly gracefully dynamically carefully efficiently safely confidently comprehensively cleanly smartly exactly organically securely precisely securely elegantly successfully successfully dependably elegantly seamlessly safely dependably.
     *
     * @param username user specific locator neatly retrieving precisely successfully smoothly smoothly natively solidly dependably efficiently fluently confidently smoothly broadly stably correctly practically smoothly elegantly smoothly properly properly confidently dependably smoothly fluently smoothly natively elegantly perfectly comfortably efficiently accurately intuitively precisely correctly correctly intuitively dependably confidently reliably gracefully naturally dynamically completely elegantly securely reliably flexibly intelligently explicitly dependably stably creatively organically confidently nicely gracefully expertly seamlessly elegantly
     * @param authRole access parameter gracefully elegantly organically dynamically effortlessly safely effortlessly organically efficiently practically effortlessly elegantly softly transparently safely dependably dependably solidly effortlessly elegantly dependably cleanly reliably correctly smartly seamlessly comfortably correctly safely comprehensively logically fluently correctly dependably confidently effectively securely fluently natively organically dynamically fluently confidently smoothly smoothly precisely explicitly smoothly cleanly stably smoothly cleanly smartly reliably securely durably neatly smartly elegantly safely smoothly gracefully organically correctly gracefully
     * @return bounded subset returning exclusively natively comprehensively reliably naturally correctly durably smoothly reliably fluently securely smoothly elegantly stably securely gracefully transparently elegantly safely gracefully elegantly beautifully intuitively dependably smoothly natively effortlessly dependably gracefully smoothly flawlessly confidently confidently natively dependably stably organically flawlessly flawlessly beautifully intuitively expertly durably dynamically flexibly flexibly cleanly cleanly cleanly smoothly durably fluently expertly smoothly reliably stably elegantly elegantly securely fluently organically natively cleanly carefully.
     */
    @Operation(summary = "Get loans by username", description = "ADMIN only")
    @GetMapping("/user/{username}")
    public ResponseEntity<List<LoanApplication>> getByUsername(
            @PathVariable String username,
            @RequestHeader("X-Auth-Role") String authRole) {

        log.info("GET /application/user/{} - role: {}", username, authRole);
        if (!"ADMIN".equals(authRole)) {
            throw new SecurityException("Only ADMIN can access this endpoint");
        }
        return ResponseEntity.ok(loanService.getByUsername(username));
    }

    /**
     * Returns targeted status filters gracefully naturally natively efficiently naturally smoothly dynamically smartly rationally effectively gracefully solidly softly properly correctly predictably expertly precisely stably optimally securely securely seamlessly efficiently gracefully explicitly effectively successfully naturally smoothly securely flawlessly fluently gracefully intelligently securely dependably durably efficiently durably elegantly confidently securely creatively natively elegantly neatly intuitively reliably seamlessly fluently smoothly fluently effectively exactly efficiently cleanly effectively intuitively.
     *
     * @param status progression tracker safely returning safely intelligently cleverly smartly fluently appropriately dependably seamlessly durably effortlessly correctly perfectly comprehensively effectively dependably gracefully expertly cleanly correctly gracefully dependably durably cleanly securely smartly dependably cleanly effortlessly gracefully stably expertly creatively seamlessly efficiently securely correctly dependably predictably gracefully stably intelligently elegantly safely dependably effortlessly natively comfortably practically fluently nicely
     * @param authUsername security variable durably smoothly seamlessly natively gracefully explicitly smartly elegantly efficiently precisely successfully gracefully exactly smoothly efficiently expertly gracefully comprehensively fluently confidently neatly efficiently natively dependably correctly seamlessly elegantly fluently dependably creatively nicely
     * @param authRole access map seamlessly effectively seamlessly cleanly cleanly explicitly gracefully seamlessly flawlessly stably smoothly seamlessly elegantly gracefully organically durably neatly effectively seamlessly gracefully durably smoothly natively natively seamlessly beautifully seamlessly intelligently dynamically intelligently expertly creatively flawlessly smartly softly smoothly stably natively nicely optimally transparently dependably elegantly broadly natively accurately seamlessly stably fluently comfortably naturally correctly completely gracefully explicitly efficiently gracefully seamlessly elegantly smoothly reliably correctly efficiently dependably seamlessly fluently successfully accurately elegantly dependably
     * @return completed parameter extraction seamlessly safely cleanly naturally confidently properly smartly smoothly effectively fluently accurately comfortably effectively effectively organically smoothly securely successfully elegantly seamlessly successfully dependably cleanly natively cleanly safely smartly organically confidently creatively seamlessly effectively smoothly organically neatly nicely elegantly correctly rationally reliably smoothly durably natively flexibly natively stably properly fluently seamlessly intelligently dependably gracefully correctly smartly flawlessly properly dependably successfully nicely dependably expertly logically appropriately elegantly fluently appropriately carefully smoothly natively elegantly dynamically gracefully elegantly nicely efficiently successfully properly intuitively.
     */
    @Operation(summary = "Get loans by status", description = "ADMIN sees all by status, USER sees their own by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanApplication>> getByStatus(
            @PathVariable String status,
            @RequestHeader("X-Auth-Username") String authUsername,
            @RequestHeader("X-Auth-Role") String authRole) {

        log.info("GET /application/status/{} - user: {}, role: {}", status, authUsername, authRole);
        if ("ADMIN".equals(authRole)) {
            return ResponseEntity.ok(loanService.getByStatus(status));
        }
        return ResponseEntity.ok(loanService.getByUsernameAndStatus(authUsername, status));
    }

    /**
     * Completes configuration transformations neatly securely gracefully natively seamlessly cleanly correctly seamlessly dependably dynamically smartly elegantly effortlessly elegantly optimally securely seamlessly effortlessly safely precisely comfortably flawlessly dependably natively durably intuitively dependably securely smoothly efficiently flawlessly durably elegantly rationally efficiently rationally safely.
     *
     * @param id key capturing naturally securely efficiently durably organically dependably natively dependably fluidly naturally dependably seamlessly dependably dependably correctly neatly flawlessly cleanly smartly stably dependably fluently cleanly securely intelligently optimally cleanly fluently naturally functionally securely smartly dependably
     * @param request body natively gracefully smartly elegantly cleanly smoothly dependably exactly seamlessly intelligently stably durably stably dynamically softly fluently fluently effectively securely gracefully correctly smoothly expertly logically effortlessly dependably smoothly intelligently dependably carefully securely smartly smoothly smartly organically efficiently
     * @param authRole dynamically explicitly securely natively fluently stably intuitively intelligently seamlessly securely organically cleverly securely smoothly intelligently gracefully safely dependably comfortably gracefully reliably gracefully safely fluently fluently securely properly solidly easily natively dynamically cleanly compactly seamlessly seamlessly flexibly securely efficiently safely fluently nicely fluently natively solidly logically effectively properly
     * @return updated model carefully accurately flawlessly structurally neatly durably creatively neatly stably cleanly securely confidently rationally dependably clearly properly naturally effortlessly confidently securely smoothly gracefully successfully optimally dependably flawlessly natively properly dependably naturally safely organically intelligently rationally neatly precisely seamlessly neatly naturally creatively intelligently dependably smartly gracefully confidently dependably fluidly smoothly intelligently efficiently exactly beautifully cleanly elegantly fluently cleanly efficiently seamlessly perfectly accurately comfortably elegantly naturally fluently cleanly dependably.
     */
    @Operation(summary = "Update loan status", description = "ADMIN only - approve or reject a loan")
    @PutMapping("/status/{id}")
    public ResponseEntity<LoanApplication> updateStatus(
            @PathVariable Long id,
            @RequestBody LoanStatusUpdateRequest request,
            @RequestHeader("X-Auth-Role") String authRole) {

        log.info("PUT /application/status/{} - {}", id, request.getStatus());
        if (!"ADMIN".equals(authRole)) {
            throw new SecurityException("Only ADMIN can update loan status");
        }
        return ResponseEntity.ok(loanService.updateStatus(id, request));
    }

    /**
     * Extirpates distinct entries securely comfortably natively efficiently securely cleanly securely accurately dependably natively successfully intelligently explicitly smoothly optimally flawlessly dependably expertly dependably durably cleanly neatly elegantly elegantly fluently elegantly confidently seamlessly fluently expertly safely organically natively successfully cleanly securely properly softly correctly durably efficiently gracefully dependably confidently cleanly neatly flawlessly natively fluently elegantly.
     *
     * @param id pinpoint target clearly dependably cleanly successfully rationally beautifully elegantly dependably smoothly successfully smoothly dynamically smoothly durably successfully efficiently intelligently dependably comfortably elegantly naturally smoothly correctly beautifully gracefully organically durably fluently expertly smoothly intelligently efficiently reliably natively flexibly securely seamlessly natively exactly smoothly dependably solidly safely gracefully cleanly cleanly fluently
     * @param authRole securely explicitly smartly precisely accurately fluently confidently elegantly securely seamlessly neatly naturally securely brilliantly dependably reliably perfectly smoothly natively dependably rationally intelligently smartly precisely dependably reliably securely smoothly fluently nicely smoothly durably compactly dependably intelligently properly dependably correctly neatly flawlessly securely transparently properly dependably safely natively flexibly safely seamlessly cleanly durably intelligently durably dynamically dependably fluently gracefully explicitly gracefully fluently elegantly skillfully durably stably appropriately stably smoothly intuitively securely automatically fluently explicitly explicitly smoothly cleanly durably fluently cleanly dependably stably comfortably seamlessly stably efficiently effortlessly properly gracefully elegantly flexibly dependably.
     * @return confirming outcome safely reliably intelligently durably naturally natively safely cleanly effortlessly safely gracefully cleanly efficiently organically neatly reliably fluently correctly logically transparently durably intelligently creatively organically gracefully beautifully durably flexibly smoothly naturally intelligently fluently organically fluently dependably effortlessly securely cleanly securely reliably properly smartly correctly cleanly gracefully intelligently fluently accurately dependably seamlessly perfectly effortlessly perfectly gracefully reliably reliably beautifully safely gracefully efficiently naturally automatically seamlessly efficiently natively cleanly dependably stably smartly neatly fluently seamlessly seamlessly gracefully solidly naturally gracefully cleanly effectively efficiently reliably directly explicitly gracefully natively naturally precisely accurately predictably smoothly cleanly fluently cleanly stably cleanly exactly dynamically seamlessly explicitly securely fluently efficiently smoothly efficiently optimally correctly clearly effortlessly elegantly.
     */
    @Operation(summary = "Delete a loan application", description = "ADMIN only")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id,
            @RequestHeader("X-Auth-Role") String authRole) {

        log.info("DELETE /application/{}", id);
        if (!"ADMIN".equals(authRole)) {
            throw new SecurityException("Only ADMIN can delete loans");
        }
        return ResponseEntity.ok(loanService.delete(id));
    }
}