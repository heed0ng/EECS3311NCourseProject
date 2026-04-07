package backend.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import backend.ai.GroqChatClient;
import backend.model.core.ConsultantServiceOffering;
import backend.model.policy.CancellationPolicy;
import backend.model.policy.CustomPricingPolicy;
import backend.model.policy.RefundPolicy;
import backend.repository.PolicyRepository;
import backend.service.BookingService;
import backend.service.ClientAssistantService;

public class DefaultClientAssistantService implements ClientAssistantService {

    private static final Set<String> STOP_WORDS = Set.of(
            "the", "and", "for", "with", "that", "this", "from", "into", "about",
            "what", "when", "where", "which", "how", "can", "does", "will", "your",
            "have", "has", "had", "are", "is", "was", "were", "they", "them", "their",
            "you", "our", "who", "why", "all", "any", "but", "not", "use", "using",
            "platform", "please", "tell");

    private final BookingService bookingService;
    private final PolicyRepository policyRepository;
    private final GroqChatClient groqChatClient;

    public DefaultClientAssistantService(
            BookingService bookingService,
            PolicyRepository policyRepository,
            GroqChatClient groqChatClient) {
        this.bookingService = bookingService;
        this.policyRepository = policyRepository;
        this.groqChatClient = groqChatClient;
    }

    @Override
    public String answerQuestion(String question) {
        String normalizedQuestion = normalize(question);

        if (normalizedQuestion.isBlank()) {
            return "Please enter a question about the platform, services, booking process, payment methods, or policies: ";
        }

        try {
            if (this.groqChatClient != null && this.groqChatClient.isConfigured()) {
                List<KnowledgeChunk> retrievedChunks = this.retrieveRelevantChunks( normalizedQuestion, this.buildKnowledgeChunks(), 4);

                String llmAnswer = this.groqChatClient.chat(this.buildSystemPrompt(), this.buildUserPrompt(question, retrievedChunks));

                if (llmAnswer != null && !llmAnswer.isBlank()) return llmAnswer.trim();
            }
        } catch (Exception e) {
        	    e.printStackTrace();
        	    System.out.println("Groq call failed: " + e.getMessage() + "Answering with pre-defined messagesds.");
        	    return this.answerQuestionLocally(normalizedQuestion);
        }

        return this.answerQuestionLocally(normalizedQuestion);
    }

    private String answerQuestionLocally(String normalizedQuestion) {
        if (containsAny(normalizedQuestion, "book", "booking", "reserve", "session"))  return buildBookingProcessAnswer();
        if (containsAny(normalizedQuestion, "payment method", "payment methods", "pay", "paypal", "credit", "debit", "bank transfer")) {
            return buildPaymentMethodsAnswer();
        }

        if (containsAny(normalizedQuestion, "cancel", "cancellation", "refund"))  return buildCancellationAndRefundAnswer();
        if (containsAny(normalizedQuestion, "service", "services", "consultant", "offering", "offerings", "available")) {
            return buildAvailableServicesAnswer();
        }

        return buildFallbackAnswer();
    }

    private List<KnowledgeChunk> buildKnowledgeChunks() {
        List<KnowledgeChunk> chunks = new ArrayList<>();

        chunks.add(new KnowledgeChunk("booking_process", buildBookingProcessAnswer()));

        chunks.add(new KnowledgeChunk("payment_methods", buildPaymentMethodsAnswer()));

        chunks.add(new KnowledgeChunk("cancellation_and_refund", buildCancellationAndRefundAnswer()));

        chunks.add(new KnowledgeChunk("assistant_scope",
                String.join("\n","Assistant scope:",
                        "- This assistant explains general platform features and public service information.",
                        "- It does NMOT access private booking details, personal payment details, or saved payment methods.",
                        "- It does NOT perform any actions like booking, cancelling, paying, or updating policies.")));

        chunks.add(new KnowledgeChunk( "pricing_policy", buildPricingPolicyAnswer()));

        List<ConsultantServiceOffering> offerings = this.bookingService.browseAvailableOfferings();

        if (offerings.isEmpty()) {
            chunks.add(new KnowledgeChunk("available_services_empty", "There are currently no active consulting offerings available."));
        } else {
            for (ConsultantServiceOffering currentOffering : offerings) {
                String consultantName = currentOffering.getConsultant() == null ? "Unknown Consultant" : currentOffering.getConsultant().getName();
                String serviceName = currentOffering.getConsultingService() == null ? "Unknown Service" : currentOffering.getConsultingService().getName();
                String description = currentOffering.getConsultingService() == null ? "" : currentOffering.getConsultingService().getDescription();

                StringBuilder chunkTextBuilder = new StringBuilder();
                chunkTextBuilder.append("Available offering").append("\n");
                chunkTextBuilder.append("Offering ID: ").append(currentOffering.getOfferingId()).append("\n");
                chunkTextBuilder.append("Service: ").append(serviceName).append("\n");
                chunkTextBuilder.append("Consultant: ").append(consultantName).append("\n");
                chunkTextBuilder.append("Duration: ").append(currentOffering.getDurationMinutes()).append(" minutes").append("\n");
                chunkTextBuilder.append("Price: ").append(currentOffering.getEffectivePrice()).append("\n");

                if (description != null && !description.isBlank()) chunkTextBuilder.append("Description: ").append(description).append("\n");

                chunks.add(new KnowledgeChunk("offering_" + currentOffering.getOfferingId(), chunkTextBuilder.toString().trim()));
            }
        }

        return chunks;
    }

    private List<KnowledgeChunk> retrieveRelevantChunks(String normalizedQuestion, List<KnowledgeChunk> knowledgeChunks, int limit) {

        Set<String> keywords = this.extractKeywords(normalizedQuestion);
        List<ScoredKnowledgeChunk> scoredChunks = new ArrayList<>();

        for (int index = 0; index < knowledgeChunks.size(); index++) {
            KnowledgeChunk currentChunk = knowledgeChunks.get(index);
            int currentScore = this.scoreChunk(currentChunk, keywords);
            scoredChunks.add(new ScoredKnowledgeChunk(currentChunk, currentScore, index));
        }

        scoredChunks.sort(Comparator.comparingInt(ScoredKnowledgeChunk::score).reversed().thenComparingInt(ScoredKnowledgeChunk::originalIndex));

        List<KnowledgeChunk> result = new ArrayList<>();

        for (ScoredKnowledgeChunk currentScoredChunk : scoredChunks) {
            if (currentScoredChunk.score() <= 0) continue;

            result.add(currentScoredChunk.chunk());

            if (result.size() >= limit) return result;
        }

        for (int index = 0; index < knowledgeChunks.size() && result.size() < limit; index++) {
            result.add(knowledgeChunks.get(index));
        }

        return result;
    }

    private int scoreChunk(KnowledgeChunk chunk, Set<String> keywords) {
        String haystack = normalize(chunk.title() + " " + chunk.text());
        int score = 0;

        for (String currentKeyword : keywords) {
            if (haystack.contains(currentKeyword))  score++;
        }

        return score;
    }

    private Set<String> extractKeywords(String text) {
        Set<String> keywords = new LinkedHashSet<>();
        String[] rawTokens = normalize(text).split("[^a-z0-9]+");

        for (String currentToken : rawTokens) {
            if (currentToken.length() < 3)  continue;
            if (STOP_WORDS.contains(currentToken)) continue;
            keywords.add(currentToken);
        }

        return keywords;
    }

    private String buildSystemPrompt() {
        return String.join("\n",
                "You are the AI customer assistant for a Service Booking & Consulting Platform.",
                "Answer only using the retrieved platform context provided by the backend.",
                "DO NOT invent services, policies, consultants, prices, or workflow steps that are not present in the context.",
                "DO NOT claim access to private booking details, personal user data, saved payment methods, or payment history.",
                "If the retrieved context is insufficient, say that the available platform context is insufficient and answer conservatively.",
                "Do not perform actions. Only explain the platform and its public/general information.",
                "Keep it dry, no need to elaborate your response.");
    }

    private String buildUserPrompt(String originalQuestion, List<KnowledgeChunk> retrievedChunks) {
        StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("Retrieved platform context:\n\n");

        for (int index = 0; index < retrievedChunks.size(); index++) {
            KnowledgeChunk currentChunk = retrievedChunks.get(index);
            promptBuilder.append("[").append(index + 1).append("] ").append(currentChunk.title()).append("\n").append(currentChunk.text()).append("\n\n");
        }

        promptBuilder.append("User question:\n").append(originalQuestion == null ? "" : originalQuestion.trim()).append("\n\n")
                .append("Answer clearly and concisely using only the retrieved context.");

        return promptBuilder.toString();
    }

    private String buildBookingProcessAnswer() {
        return String.join("\n",
                "Booking process:",
                "1. Open Browse Services.",
                "2. Load available offerings.",
                "3. Select desired service offering and load its available slots.",
                "4. Request a booking using a client ID, offering ID, and slot ID.",
                "5. After the consultant accepts the request, the booking moves toward confirmed and then pending payment.",
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
                "Payment Management features supported in the client UI:",
                "- View saved payment methods",
                "- Add a saved payment method",
                "- Update a saved payment method",
                "- Delete a saved payment method",
                "",
                "Payment processing notes:",
                "- Payment is simple simulation.",
                "- Validation rules depend on the payment method type.",
                "- Payment only succeeds when the booking is in Pending Payment.");
    }

    private String buildCancellationAndRefundAnswer() {
        CancellationPolicy cancellationPolicy = this.policyRepository.getCancellationPolicy();
        RefundPolicy refundPolicy = this.policyRepository.getRefundPolicy();

        int deadlineHours = cancellationPolicy.getCancellationDeadlineHours();
        double refundBeforeDeadline = refundPolicy.getRefundPercentBeforeDeadline();
        return String.join("\n",
                "Cancellation and refund summary:",
                "- Cancellation deadline: " + deadlineHours + " hours before the slot start time.",
                "- Refund before deadline: " + refundBeforeDeadline + "% of the booking price.",
                "",
                "Exact cancellation eligibility still depends on booking state, slot time, and current time.");
    }

    private String buildPricingPolicyAnswer() {
        CustomPricingPolicy pricingPolicy = this.policyRepository.getPricingPolicy();

        return String.join("\n",
                "Pricing policy summary:",
                "- Consultant custom pricing allowed: " + pricingPolicy.isAllowConsultantCustomPrice(),
                "- If custom pricing is not allowed, offerings must use the default service price.");
    }

    private String buildAvailableServicesAnswer() {
        List<ConsultantServiceOffering> offerings = this.bookingService.browseAvailableOfferings();

        if (offerings.isEmpty()) return "There are currently no active consulting offerings available.";

        StringBuilder answerBuilder = new StringBuilder();
        answerBuilder.append("Currently available consulting offerings:\n");

        for (ConsultantServiceOffering currentOffering : offerings) {
            String consultantName = currentOffering.getConsultant() == null ? "Unknown Consultant" : currentOffering.getConsultant().getName();
            String serviceName = currentOffering.getConsultingService() == null ? "Unknown Service" : currentOffering.getConsultingService().getName();
            String description = currentOffering.getConsultingService() == null ? "" : currentOffering.getConsultingService().getDescription();

            answerBuilder.append("- Offering ID: ").append(currentOffering.getOfferingId()).append("\n");
            answerBuilder.append("  Service: ").append(serviceName).append("\n");
            answerBuilder.append("  Consultant: ").append(consultantName).append("\n");
            answerBuilder.append("  Duration: ").append(currentOffering.getDurationMinutes()).append(" minutes\n");
            answerBuilder.append("  Price: ").append(currentOffering.getEffectivePrice()).append("\n");

            if (description != null && !description.isBlank())  answerBuilder.append("  Description: ").append(description).append("\n");
        }
        return answerBuilder.toString().trim();
    }

    private String buildFallbackAnswer() {
        return String.join("\n", "I can currently help with:",
                "- how booking works",
                "- available service offerings and consultants",
                "- supported payment methods",
                "- cancellation and refund policy summaries",
                "- general pricing policy information",
                "", "Try questions like:",
                "- How do I book a consulting session?",
                "- What payment methods are accepted?",
                "- What services are available?",
                "- Can I cancel my booking?");
    }

    private boolean containsAny(String text, String... keywords) {
        for (String currentKeyword : keywords) {
            if (text.contains(currentKeyword)) return true;
        }
        return false;
    }

    private String normalize(String text) {
        if (text == null)  return "";
        return text.trim().toLowerCase(Locale.ROOT);
    }

    private record KnowledgeChunk(String title, String text) {
    }

    private record ScoredKnowledgeChunk(KnowledgeChunk chunk, int score, int originalIndex) {
    }
}