package backend.api.dto.response;

public class AvailabilitySlotResponse {

    private String slotId;
    private String offeringId;
    private String consultantId;
    private String startDateTime;
    private String endDateTime;
    private String status;

    public AvailabilitySlotResponse() {
    }

    public AvailabilitySlotResponse(
            String slotId,
            String offeringId,
            String consultantId,
            String startDateTime,
            String endDateTime,
            String status) {
        this.slotId = slotId;
        this.offeringId = offeringId;
        this.consultantId = consultantId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
    }

    public String getSlotId() {
        return this.slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getOfferingId() {
        return this.offeringId;
    }

    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    public String getConsultantId() {
        return this.consultantId;
    }

    public void setConsultantId(String consultantId) {
        this.consultantId = consultantId;
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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}