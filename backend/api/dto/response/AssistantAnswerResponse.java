package backend.api.dto.response;

public class AssistantAnswerResponse {

    private String answer;

    public AssistantAnswerResponse() {
    }

    public AssistantAnswerResponse(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}