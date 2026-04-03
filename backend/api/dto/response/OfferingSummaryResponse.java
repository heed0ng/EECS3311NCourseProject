package backend.api.dto.response;

public class OfferingSummaryResponse {

    private String offeringId;
    private String serviceName;
    private String consultantId;
    private String consultantName;
    private Integer durationMinutes;
    private Double basePrice;
    private String description;

    public OfferingSummaryResponse() {
    }

    public OfferingSummaryResponse(
            String offeringId,
            String serviceName,
            String consultantId,
            String consultantName,
            Integer durationMinutes,
            Double basePrice,
            String description) {
        this.offeringId = offeringId;
        this.serviceName = serviceName;
        this.consultantId = consultantId;
        this.consultantName = consultantName;
        this.durationMinutes = durationMinutes;
        this.basePrice = basePrice;
        this.description = description;
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

    public String getConsultantId() {
        return this.consultantId;
    }

    public void setConsultantId(String consultantId) {
        this.consultantId = consultantId;
    }

    public String getConsultantName() {
        return this.consultantName;
    }

    public void setConsultantName(String consultantName) {
        this.consultantName = consultantName;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getBasePrice() {
        return this.basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}