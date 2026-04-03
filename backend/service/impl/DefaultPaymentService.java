package service.impl;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import model.notification.PaymentProcessedEvent;
import model.payment.PaymentTransaction;
import model.payment.SavedPaymentMethod;
import observer.EventPublisher;
import paymentStrategy.PaymentStrategyFactory;
import repository.BookingRepository;
import repository.ClientRepository;
import repository.PaymentTransactionRepository;
import repository.PolicyRepository;
import repository.SavedPaymentMethodRepository;
import repository.sqlite.IdGenerator;
import service.PaymentService;
import util.*;

public class DefaultPaymentService implements PaymentService {
    private final IdGenerator idGenerator;
    private final ClientRepository clientRepository;
    private final BookingRepository bookingRepository;
    private final SavedPaymentMethodRepository savedPaymentMethodRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PolicyRepository policyRepository;
    private final EventPublisher eventPublisher;
    private final PaymentStrategyFactory paymentStrategyFactory;

    public DefaultPaymentService(ClientRepository clientRepository, BookingRepository bookingRepository, SavedPaymentMethodRepository savedPaymentMethodRepository,
    		PaymentTransactionRepository paymentTransactionRepository, PolicyRepository policyRepository, EventPublisher eventPublisher,
    		PaymentStrategyFactory paymentStrategyFactory, IdGenerator idGenerator) {
        this.clientRepository = clientRepository;
        this.bookingRepository = bookingRepository;
        this.savedPaymentMethodRepository = savedPaymentMethodRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.policyRepository = policyRepository;
        this.eventPublisher = eventPublisher;
        this.paymentStrategyFactory = paymentStrategyFactory;
        this.idGenerator = idGenerator;
    }

    @Override
    public SavedPaymentMethod addSavedPaymentMethod(String clientId, PaymentMethodType methodType, String displayLabel, String paymentDetails, String paymentDetailData) {
        var client = this.clientRepository.findById(clientId).orElseThrow(() -> new EntityNotFoundException("Client not found."));
        this.validatePaymentInput(methodType, paymentDetails, paymentDetailData);

        String savedMethodId = this.idGenerator.nextId("saved_payment_methods", "saved_method_id", "paymentmethod");
        SavedPaymentMethod method = new SavedPaymentMethod(savedMethodId, client, methodType, displayLabel, paymentDetails, paymentDetailData);
        this.savedPaymentMethodRepository.save(method);
        return method;
    }

    @Override
    public SavedPaymentMethod updateSavedPaymentMethod(String clientId, String savedMethodId, String displayLabel, String paymentDetails, String paymentDetailData) {
        SavedPaymentMethod method = this.savedPaymentMethodRepository.findById(savedMethodId).orElseThrow(() -> new EntityNotFoundException("Saved payment method not found."));
        if (!method.getClient().getUserId().equals(clientId)) throw new BusinessRuleViolationException("Payment method does not belong to client.");
        this.validatePaymentInput(method.getMethodType(), paymentDetails, paymentDetailData);

        method.setDisplayLabel(requireNonBlank(displayLabel, "Display label is required."));
        method.setPaymentDetails(requireNonBlank(paymentDetails, "Payment details are required."));
        method.setPaymentDetailData(paymentDetailData == null ? "" : paymentDetailData.trim());

        this.savedPaymentMethodRepository.save(method);
        return method;
    }

    @Override
    public void removeSavedPaymentMethod(String clientId, String savedMethodId) {
        SavedPaymentMethod method = savedPaymentMethodRepository.findById(savedMethodId).orElseThrow(() -> new EntityNotFoundException("Saved payment method not found."));
        if (!method.getClient().getUserId().equals(clientId)) throw new BusinessRuleViolationException("Payment method does not belong to client.");

        this.savedPaymentMethodRepository.delete(savedMethodId);
    }

    @Override
    public List<SavedPaymentMethod> getSavedPaymentMethods(String clientId) {
        return savedPaymentMethodRepository.findByClient(clientId);
    }

    @Override
    public PaymentTransaction processPayment(String clientId, String bookingId, String savedMethodId) {
        var booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found."));
        if (!booking.belongsToClient(clientId)) throw new BusinessRuleViolationException("Booking does not belong to client.");
        if (!"Pending Payment".equals(booking.getStateName())) throw new BusinessRuleViolationException("Booking is not pending payment.");

        SavedPaymentMethod method = this.savedPaymentMethodRepository.findById(savedMethodId).orElseThrow(() -> new EntityNotFoundException("Saved payment method not found."));
        if (!method.getClient().getUserId().equals(clientId)) throw new BusinessRuleViolationException("Payment method does not belong to client.");

        var strategy = this.paymentStrategyFactory.create(method.getMethodType());
        strategy.validate(method);
        strategy.process(booking, method);

        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 4000));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Payment simulation was interrupted.", exception);
        }

        booking.markPaid();
        this.bookingRepository.save(booking);

        PaymentTransaction pendingTransaction = this.findPendingPaymentTransactionForBooking(bookingId);
        PaymentTransaction transactionToSave;

        if (pendingTransaction != null) {
            transactionToSave = new PaymentTransaction(pendingTransaction.getTransactionId(), booking, booking.getClient(), PaymentTransactionType.PAYMENT,
                    PaymentTransactionStatus.SUCCESS, method.getMethodType(), booking.getAgreedPrice(), pendingTransaction.getCreatedAt()
            );
        } else {
            String transactionId = idGenerator.nextId("payment_transactions", "transaction_id", "transaction");
            transactionToSave = new PaymentTransaction(transactionId, booking, booking.getClient(), PaymentTransactionType.PAYMENT,
                    PaymentTransactionStatus.SUCCESS, method.getMethodType(), booking.getAgreedPrice(), LocalDateTime.now());
        }

        this.paymentTransactionRepository.save(transactionToSave);

        if (policyRepository.getNotificationPolicy().isNotifyOnPaymentProcessed()) {
            this.eventPublisher.publish(new PaymentProcessedEvent(this.eventPublisher.nextEventId(), LocalDateTime.now(),
                    "Payment processed successfully for booking " + booking.getBookingId() + "."));
        }
        return transactionToSave;
    }

    @Override
    public List<PaymentTransaction> getPaymentHistory(String clientId) {
        return paymentTransactionRepository.findByClient(clientId);
    }

    private PaymentTransaction findPendingPaymentTransactionForBooking(String bookingId) {
        return paymentTransactionRepository.findByBooking(bookingId).stream()
                .filter(transaction -> transaction.getTransactionType() == PaymentTransactionType.PAYMENT && transaction.getStatus() == PaymentTransactionStatus.PENDING)
                .findFirst().orElse(null);
    }

    private void validatePaymentInput(PaymentMethodType methodType, String paymentDetails, String paymentDetailData) {
        String details = requireNonBlank(paymentDetails, "Payment details are required.");
        String metadata = paymentDetailData == null ? "" : paymentDetailData.trim();

        switch (methodType) {
            case CREDIT_CARD:
            case DEBIT_CARD: {
                String cardNumber = digitsOnly(details);
                if (!cardNumber.matches("\\d{16}")) throw new BusinessRuleViolationException(methodType.name() + " number must contain exactly 16 digits.");
                String[] parts = metadata.split("\\|");
                if (parts.length != 2) throw new BusinessRuleViolationException(methodType.name() + " expiry and cvv must be in MM/YY|CVV format.");

                String expiry = parts[0].trim();
                String cvv = parts[1].trim();

                if (!expiry.matches("\\d{2}/\\d{2}")) throw new BusinessRuleViolationException(methodType.name() + " expiry must be in MM/YY format.");
                if (!cvv.matches("\\d{3,4}"))  throw new BusinessRuleViolationException(methodType.name() + " CVV must contain 3 or 4 digits.");

                try {
                    YearMonth expiryMonth = YearMonth.parse(expiry, DateTimeFormatter.ofPattern("MM/yy"));
                    if (expiryMonth.isBefore(YearMonth.now())) throw new BusinessRuleViolationException(methodType.name() + " expiry must be current or future.");
                } catch (DateTimeParseException exception) {
                    throw new BusinessRuleViolationException(methodType.name() + " expiry must be in MM/YY format.");
                }
                break;
            }

            case PAYPAL:
                if (!details.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) throw new BusinessRuleViolationException("PayPal email format is invalid.");
                break;

            case BANK_TRANSFER: {
                String accountNumber = digitsOnly(details);
                String routingNumber = digitsOnly(metadata);

                if (!accountNumber.matches("\\d{6,17}"))  throw new BusinessRuleViolationException("Bank account number must contain 6 to 17 digits.");
                if (!routingNumber.matches("\\d{9}")) throw new BusinessRuleViolationException("Routing number must contain exactly 9 digits.");
                break;
            }

            case NONE_SELECTED:
            default:
                throw new BusinessRuleViolationException("Unsupported payment method type.");
        }
    }

    private String requireNonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) throw new BusinessRuleViolationException(message);
        return value.trim();
    }

    private String digitsOnly(String value) {
        return value.replaceAll("\\D", "");
    }
}