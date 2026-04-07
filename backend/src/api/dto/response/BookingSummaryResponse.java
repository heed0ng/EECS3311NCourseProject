package backend.api.dto.response;

public class BookingSummaryResponse {

    private String bookingId;
    private String clientId;
    private String offeringId;
    private String serviceName;
    private String consultantName;
    private String slotId;
    private String startDateTime;
    private String endDateTime;
    private String bookingStatus;
    private Double price;

    public BookingSummaryResponse() {
    }

    public BookingSummaryResponse(
            String bookingId,
            String clientId,
            String offeringId,
            String serviceName,
            String consultantName,
            String slotId,
            String startDateTime,
            String endDateTime,
            String bookingStatus,
            Double price) {
        this.bookingId = bookingId;
        this.clientId = clientId;
        this.offeringId = offeringId;
        this.serviceName = serviceName;
        this.consultantName = consultantName;
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

    public String getConsultantName() {
        return this.consultantName;
    }

    public void setConsultantName(String consultantName) {
        this.consultantName = consultantName;
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