package backend.api.dto.request;

public class AssistantQuestionRequest {

    private String question;

    public AssistantQuestionRequest() {
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}