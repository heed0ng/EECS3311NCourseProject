package backend.ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GroqChatClient {

    private static final String GROQ_CHAT_COMPLETIONS_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GroqChatClient(String apiKey, String model) {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
        this.model = (model == null || model.isBlank()) ? "llama-3.3-70b-versatile" : model.trim();
    }

    public boolean isConfigured() {
        return this.apiKey != null && !this.apiKey.isBlank();
    }

    public String chat(String systemPrompt, String userPrompt) {
        if (!this.isConfigured())  throw new IllegalStateException("GROQ_API_KEY is not configured.");

        try {
            ObjectNode requestBody = this.objectMapper.createObjectNode();
            requestBody.put("model", this.model);
            requestBody.put("temperature", 0.1);

            ArrayNode messages = requestBody.putArray("messages");

            ObjectNode systemMessage = messages.addObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);

            ObjectNode userMessage = messages.addObject();
            userMessage.put("role", "user");
            userMessage.put("content", userPrompt);

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(GROQ_CHAT_COMPLETIONS_URL))
                    .timeout(Duration.ofSeconds(30)).header("Authorization", "Bearer " + this.apiKey).header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(this.objectMapper.writeValueAsString(requestBody), StandardCharsets.UTF_8)).build();

            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            JsonNode root = this.objectMapper.readTree(response.body());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException( "Groq API request failed: " + this.extractErrorMessage(root, response.body()));
            }

            JsonNode contentNode = root.path("choices").path(0).path("message").path("content");

            if (contentNode.isMissingNode() || contentNode.asText().isBlank())  throw new RuntimeException("Groq API returned an empty answer.");

            return contentNode.asText().trim();

        } catch (Exception exception) {
            throw new RuntimeException("Failed to get Groq assistant answer.", exception);
        }
    }

    private String extractErrorMessage(JsonNode root, String rawBody) {
        JsonNode errorMessageNode = root.path("error").path("message");

        if (!errorMessageNode.isMissingNode() && !errorMessageNode.asText().isBlank())  return errorMessageNode.asText();

        if (rawBody == null || rawBody.isBlank())  return "Unknown error.";

        return rawBody;
    }
}