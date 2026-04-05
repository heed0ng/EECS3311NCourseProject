package backend.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.request.ApproveConsultantRequest;
import backend.api.dto.request.RejectConsultantRequest;
import backend.api.dto.response.ActionResultResponse;
import backend.api.dto.response.PendingConsultantResponse;
import backend.model.user.Consultant;
import backend.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminConsultantController {

    private final AdminService adminService;

    public AdminConsultantController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/consultants/pending")
    public ResponseEntity<List<PendingConsultantResponse>> getPendingConsultants() {
        List<PendingConsultantResponse> responses = new ArrayList<>();

        try {
            List<Consultant> pendingConsultants = this.adminService.getPendingConsultants();

            for (Consultant currentConsultant : pendingConsultants) {
                responses.add(toPendingConsultantResponse(currentConsultant));
            }

            return ResponseEntity.ok(responses);

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(responses);
        }
    }

    @PostMapping("/consultants/{consultantId}/approve")
    public ResponseEntity<ActionResultResponse> approveConsultant(
            @PathVariable String consultantId,
            @RequestBody ApproveConsultantRequest approveConsultantRequest) {

        try {
            this.adminService.approveConsultant(
                    approveConsultantRequest.getAdminId(),
                    consultantId);

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Consultant approved successfully."));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    @PostMapping("/consultants/{consultantId}/reject")
    public ResponseEntity<ActionResultResponse> rejectConsultant(
            @PathVariable String consultantId,
            @RequestBody RejectConsultantRequest rejectConsultatntRequest) {

        try {
            this.adminService.rejectConsultant(
                    rejectConsultatntRequest.getAdminId(),
                    consultantId);

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Consultant rejected successfully."));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    private PendingConsultantResponse toPendingConsultantResponse(Consultant consultant) {
        return new PendingConsultantResponse(
                consultant.getUserId(),
                consultant.getName(),
                consultant.getEmail(),
                consultant.getApprovalStatus().toString());
    }
}