package backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.request.UpdateCancellationPolicyRequest;
import backend.api.dto.request.UpdateNotificationPolicyRequest;
import backend.api.dto.request.UpdatePricingPolicyRequest;
import backend.api.dto.request.UpdateRefundPolicyRequest;
import backend.api.dto.response.ActionResultResponse;
import backend.api.dto.response.PolicySummaryResponse;
import backend.api.mapper.AdminDtoMapper;
import backend.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminPolicyController {

    private final AdminService adminService;

    public AdminPolicyController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/policies")
    public ResponseEntity<?> getPolicySummary() {
        try {
            PolicySummaryResponse response = AdminDtoMapper.toPolicySummaryResponse(
                    this.adminService.getCancellationPolicy(),
                    this.adminService.getPricingPolicy(),
                    this.adminService.getNotificationPolicy(),
                    this.adminService.getRefundPolicy());

            return ResponseEntity.ok(response);

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    @PostMapping("/policies/cancellation")
    public ResponseEntity<ActionResultResponse> updateCancellationPolicy(
            @RequestBody UpdateCancellationPolicyRequest request) {

        try {
            this.adminService.updateCancellationPolicy(
                    request.getAdminId(),
                    request.getCancellationDeadlineHours());

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Cancellation policy updated successfully."));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    @PostMapping("/policies/refund")
    public ResponseEntity<ActionResultResponse> updateRefundPolicy(
            @RequestBody UpdateRefundPolicyRequest request) {

        try {
            this.adminService.updateRefundPolicy(
                    request.getAdminId(),
                    request.getRefundPercentBeforeDeadline(),
                    request.getRefundPercentAfterDeadline());

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Refund policy updated successfully."));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    @PostMapping("/policies/pricing")
    public ResponseEntity<ActionResultResponse> updatePricingPolicy(
            @RequestBody UpdatePricingPolicyRequest request) {

        try {
            this.adminService.updatePricingPolicy(
                    request.getAdminId(),
                    request.isAllowConsultantCustomPrice());

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Pricing policy updated successfully."));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    @PostMapping("/policies/notifications")
    public ResponseEntity<ActionResultResponse> updateNotificationPolicy(
            @RequestBody UpdateNotificationPolicyRequest request) {

        try {
            this.adminService.updateNotificationPolicy(
                    request.getAdminId(),
                    request.isNotifyOnBookingRequested(),
                    request.isNotifyOnBookingAccepted(),
                    request.isNotifyOnBookingRejected(),
                    request.isNotifyOnPaymentProcessed(),
                    request.isNotifyOnBookingCancelled(),
                    request.isNotifyOnConsultantApprovalDecision());

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Notification policy updated successfully."));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }
}