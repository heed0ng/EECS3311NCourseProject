package backend.api.dto.response;

public class CancellationSummaryResponse {

    private String bookingId;
    private String summaryMessage;

    public CancellationSummaryResponse() {
    }

    public CancellationSummaryResponse(String bookingId, String summaryMessage) {
        this.bookingId = bookingId;
        this.summaryMessage = summaryMessage;
    }

    public String getBookingId() {
        return this.bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getSummaryMessage() {
        return this.summaryMessage;
    }

    public void setSummaryMessage(String summaryMessage) {
        this.summaryMessage = summaryMessage;
    }
}