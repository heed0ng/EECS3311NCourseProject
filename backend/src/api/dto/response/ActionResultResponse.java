package backend.api.dto.response;

public class ActionResultResponse {

    private boolean success;
    private String message;

    public ActionResultResponse() {
    }

    public ActionResultResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}