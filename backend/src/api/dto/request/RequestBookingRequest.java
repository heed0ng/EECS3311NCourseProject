package backend.api.dto.request;

public class RequestBookingRequest {

    private String clientId;
    private String offeringId;
    private String slotId;

    public RequestBookingRequest() {
    }

    public RequestBookingRequest(String clientId, String offeringId, String slotId) {
        this.clientId = clientId;
        this.offeringId = offeringId;
        this.slotId = slotId;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getOfferingId() {
        return this.offeringId;
    }

    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    public String getSlotId() {
        return this.slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }
}