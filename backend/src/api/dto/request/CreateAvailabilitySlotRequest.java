package backend.api.dto.request;

public class CreateAvailabilitySlotRequest {

    private String startDateTime;
    private String endDateTime;

    public CreateAvailabilitySlotRequest() {
    }

    public String getStartDateTime() {
        return this.startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return this.endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }
}