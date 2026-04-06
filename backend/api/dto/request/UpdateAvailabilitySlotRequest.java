package backend.api.dto.request;

public class UpdateAvailabilitySlotRequest {

    private String startDateTime;
    private String endDateTime;

    public UpdateAvailabilitySlotRequest() {
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