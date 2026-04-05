package backend.service.impl;

import java.util.List;
import java.util.Locale;

import backend.model.core.ConsultantServiceOffering;
import backend.model.policy.CancellationPolicy;
import backend.model.policy.RefundPolicy;
import backend.repository.PolicyRepository;
import backend.service.BookingService;
import backend.service.ClientAssistantService;

public class DefaultClientAssistantService implements ClientAssistantService {

    private final BookingService bookingService;
    private final PolicyRepository policyRepository;

    public DefaultClientAssistantService(
            BookingService bookingService,
            PolicyRepository policyRepository) {
        this.bookingService = bookingService;
        this.policyRepository = policyRepository;
    }

    @Override
    public String answerQuestion(String question) {
        String normalizedQuestion = normalize(question);

        if (normalizedQuestion.isBlank()) {
            return "Please enter a question about the platform, services, booking process, payment methods, or policies.";
        }

        if (containsAny(normalizedQuestion, "book", "booking", "reserve", "session")) {
            return buildBookingProcessAnswer();
        }

        if (containsAny(normalizedQuestion, "payment method", "payment methods", "pay", "paypal", "credit", "debit", "bank transfer")) {
            return buildPaymentMethodsAnswer();
        }

        if (containsAny(normalizedQuestion, "cancel", "cancellation", "refund")) {
            return buildCancellationAndRefundAnswer();
        }

        if (containsAny(normalizedQuestion, "service", "services", "consultant", "offering", "offerings", "available")) {
            return buildAvailableServicesAnswer();
        }

        return buildFallbackAnswer();
    }

    private String buildBookingProcessAnswer() {
        return String.join("\n",
                "Booking process:",
                "1. Open Browse Services.",
                "2. Load available offerings.",
                "3. Select an offering and load its available slots.",
                "4. Request a booking using a client ID, offering ID, and slot ID.",
                "5. After the consultant accepts the request, the booking moves toward payment.",
                "6. Payment can only be processed when the booking is in Pending Payment.",
                "7. After successful payment, the booking becomes Paid.",
                "8. The consultant can later mark the session as Completed.");
    }

    private String buildPaymentMethodsAnswer() {
        return String.join("\n",
                "Supported payment methods:",
                "- CREDIT_CARD",
                "- DEBIT_CARD",
                "- PAYPAL",
                "- BANK_TRANSFER",
                "",
                "Management features currently supported in the client UI:",
                "- View saved payment methods",
                "- Add a saved payment method",
                "- Update a saved payment method",
                "- Delete a saved payment method",
                "",
                "Payment processing notes:",
                "- Payment is simulated.",
                "- Validation rules depend on the payment method type.",
                "- Payment only succeeds when the booking is in Pending Payment.");
    }

    private String buildCancellationAndRefundAnswer() {
        CancellationPolicy cancellationPolicy = this.policyRepository.getCancellationPolicy();
        RefundPolicy refundPolicy = this.policyRepository.getRefundPolicy();

        int deadlineHours = cancellationPolicy.getCancellationDeadlineHours();
        double refundBeforeDeadline = refundPolicy.getRefundPercentBeforeDeadline();
        double refundAfterDeadline = refundPolicy.getRefundPercentAfterDeadline();

        return String.join("\n",
                "Cancellation and refund summary:",
                "- Cancellation deadline: " + deadlineHours + " hours before the slot start time.",
                "- Refund before deadline: " + refundBeforeDeadline + "% of the booking price.",
                "- Refund after deadline: " + refundAfterDeadline + "% of the booking price.",
                "",
                "Exact cancellation eligibility still depends on the booking time and current time.");
    }

    private String buildAvailableServicesAnswer() {
        List<ConsultantServiceOffering> offerings = this.bookingService.browseAvailableOfferings();

        if (offerings.isEmpty()) {
            return "There are currently no active consulting offerings available.";
        }

        StringBuilder answerBuilder = new StringBuilder();
        answerBuilder.append("Currently available consulting offerings:\n");

        for (ConsultantServiceOffering currentOffering : offerings) {
            String consultantName = currentOffering.getConsultant() == null
                    ? "Unknown Consultant"
                    : currentOffering.getConsultant().getName();

            String serviceName = currentOffering.getConsultingService() == null
                    ? "Unknown Service"
                    : currentOffering.getConsultingService().getName();

            String description = currentOffering.getConsultingService() == null
                    ? ""
                    : currentOffering.getConsultingService().getDescription();

            answerBuilder.append("- Offering ID: ").append(currentOffering.getOfferingId()).append("\n");
            answerBuilder.append("  Service: ").append(serviceName).append("\n");
            answerBuilder.append("  Consultant: ").append(consultantName).append("\n");
            answerBuilder.append("  Duration: ").append(currentOffering.getDurationMinutes()).append(" minutes\n");
            answerBuilder.append("  Price: ").append(currentOffering.getEffectivePrice()).append("\n");

            if (description != null && !description.isBlank()) {
                answerBuilder.append("  Description: ").append(description).append("\n");
            }
        }

        return answerBuilder.toString().trim();
    }

    private String buildFallbackAnswer() {
        return String.join("\n",
                "I can currently help with:",
                "- how booking works",
                "- available services and consultants",
                "- supported payment methods",
                "- cancellation and refund policy summaries",
                "",
                "Try questions like:",
                "- How do I book a consulting session?",
                "- What payment methods are accepted?",
                "- What services are available?",
                "- Can I cancel my booking?");
    }

    private boolean containsAny(String text, String... keywords) {
        for (String currentKeyword : keywords) {
            if (text.contains(currentKeyword)) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }

        return text.trim().toLowerCase(Locale.ROOT);
    }
}