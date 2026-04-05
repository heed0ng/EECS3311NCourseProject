package backend.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.request.AddSavedPaymentMethodRequest;
import backend.api.dto.request.UpdateSavedPaymentMethodRequest;
import backend.api.dto.response.ActionResultResponse;
import backend.api.dto.response.SavedPaymentMethodResponse;
import backend.api.mapper.PaymentDtoMapper;
import backend.model.payment.SavedPaymentMethod;
import backend.service.PaymentService;
import backend.util.PaymentMethodType;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = "*")
public class ClientPaymentMethodController {

    private final PaymentService paymentService;

    public ClientPaymentMethodController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{clientId}/payment-methods")
    public ResponseEntity<List<SavedPaymentMethodResponse>> getSavedPaymentMethods(
            @PathVariable String clientId) {

        List<SavedPaymentMethodResponse> responses = new ArrayList<>();

        try {
            List<SavedPaymentMethod> savedPaymentMethods =
                    this.paymentService.getSavedPaymentMethods(clientId);

            for (SavedPaymentMethod currentSavedPaymentMethod : savedPaymentMethods) {
                responses.add(PaymentDtoMapper.toSavedPaymentMethodResponse(currentSavedPaymentMethod));
            }

            return ResponseEntity.ok(responses);

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(responses);
        }
    }

    @PostMapping("/{clientId}/payment-methods")
    public ResponseEntity<ActionResultResponse> addSavedPaymentMethod(
            @PathVariable String clientId,
            @RequestBody AddSavedPaymentMethodRequest addSavedPaymentMethodRequest) {

        try {
            PaymentMethodType paymentMethodType =
                    PaymentMethodType.valueOf(
                            addSavedPaymentMethodRequest.getPaymentMethodType().trim().toUpperCase());

            this.paymentService.addSavedPaymentMethod(
                    clientId,
                    paymentMethodType,
                    addSavedPaymentMethodRequest.getNickname(),
                    addSavedPaymentMethodRequest.getPaymentDetails(),
                    addSavedPaymentMethodRequest.getPaymentMetadata());

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Saved payment method added successfully."));

        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, "Invalid payment method type."));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    @PutMapping("/{clientId}/payment-methods/{savedPaymentMethodId}")
    public ResponseEntity<ActionResultResponse> updateSavedPaymentMethod(
            @PathVariable String clientId,
            @PathVariable String savedPaymentMethodId,
            @RequestBody UpdateSavedPaymentMethodRequest updateSavedPaymentMethodRequest) {

        try {
            this.paymentService.updateSavedPaymentMethod(
                    clientId,
                    savedPaymentMethodId,
                    updateSavedPaymentMethodRequest.getNickname(),
                    updateSavedPaymentMethodRequest.getPaymentDetails(),
                    updateSavedPaymentMethodRequest.getPaymentMetadata());

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Saved payment method updated successfully."));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    @DeleteMapping("/{clientId}/payment-methods/{savedPaymentMethodId}")
    public ResponseEntity<ActionResultResponse> deleteSavedPaymentMethod(
            @PathVariable String clientId,
            @PathVariable String savedPaymentMethodId) {

        try {
            this.paymentService.removeSavedPaymentMethod(clientId, savedPaymentMethodId);

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Saved payment method deleted successfully."));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }
}