package backend.api.dto.response;

public class ConsultantScheduleEntryResponse {

    private String bookingId;
    private String clientId;
    private String clientName;
    private String offeringId;
    private String serviceName;
    private String slotId;
    private String startDateTime;
    private String endDateTime;
    private String bookingStatus;
    private Double price;

    public ConsultantScheduleEntryResponse() {
    }

    public ConsultantScheduleEntryResponse(
            String bookingId,
            String clientId,
            String clientName,
            String offeringId,
            String serviceName,
            String slotId,
            String startDateTime,
            String endDateTime,
            String bookingStatus,
            Double price) {
        this.bookingId = bookingId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.offeringId = offeringId;
        this.serviceName = serviceName;
        this.slotId = slotId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.bookingStatus = bookingStatus;
        this.price = price;
    }

    public String getBookingId() {
        return this.bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getOfferingId() {
        return this.offeringId;
    }

    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSlotId() {
        return this.slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
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

    public String getBookingStatus() {
        return this.bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}