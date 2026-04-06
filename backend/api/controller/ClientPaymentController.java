package backend.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.request.ProcessPaymentRequest;
import backend.api.dto.response.ActionResultResponse;
import backend.api.dto.response.PaymentTransactionResponse;
import backend.api.mapper.PaymentDtoMapper;
import backend.model.payment.PaymentTransaction;
import backend.service.PaymentService;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = "*")
public class ClientPaymentController {

    private final PaymentService paymentService;

    public ClientPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

@GetMapping("/{clientId}/payments")
public ResponseEntity<?> getPaymentHistory(@PathVariable String clientId) {
    List<PaymentTransactionResponse> responses = new ArrayList<>();

    try {
        List<PaymentTransaction> paymentTransactions =
                this.paymentService.getPaymentHistory(clientId);

        for (PaymentTransaction currentPaymentTransaction : paymentTransactions) {
            responses.add(
                    PaymentDtoMapper.toPaymentTransactionResponse(
                            currentPaymentTransaction,
                            ""));
        }

        return ResponseEntity.ok(responses);

    } catch (Exception exception) {
        return ResponseEntity.badRequest().body(
                new ActionResultResponse(false, exception.getMessage()));
    }
}

    @PostMapping("/{clientId}/payments")
    public ResponseEntity<?> processPayment(
            @PathVariable String clientId,
            @RequestBody ProcessPaymentRequest processPaymentRequest) {

        try {
            PaymentTransaction paymentTransaction = this.paymentService.processPayment(
                    clientId,
                    processPaymentRequest.getBookingId(),
                    processPaymentRequest.getSavedPaymentMethodId());

            PaymentTransactionResponse response =
                    PaymentDtoMapper.toPaymentTransactionResponse(
                            paymentTransaction,
                            "Payment processed successfully.");

            return ResponseEntity.ok(response);

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }
}