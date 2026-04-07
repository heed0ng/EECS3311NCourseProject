package backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.request.AssistantQuestionRequest;
import backend.api.dto.response.ActionResultResponse;
import backend.api.dto.response.AssistantAnswerResponse;
import backend.service.ClientAssistantService;

@RestController
@RequestMapping("/api/client/assistant")
@CrossOrigin(origins = "*")
public class ClientAssistantController {

    private final ClientAssistantService clientAssistantService;

    public ClientAssistantController(ClientAssistantService clientAssistantService) {
        this.clientAssistantService = clientAssistantService;
    }

    @PostMapping("/question")
    public ResponseEntity<?> askQuestion(
            @RequestBody AssistantQuestionRequest assistantQuestionRequest) {

        try {
            String answer = this.clientAssistantService.answerQuestion(
                    assistantQuestionRequest.getQuestion());

            return ResponseEntity.ok(new AssistantAnswerResponse(answer));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }
}