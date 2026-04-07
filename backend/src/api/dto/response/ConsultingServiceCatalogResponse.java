package backend.api.dto.response;

public class ConsultingServiceCatalogResponse {

    private String serviceId;
    private String serviceName;
    private String description;
    private Integer durationMinutes;
    private Double basePrice;

    public ConsultingServiceCatalogResponse() {
    }

    public ConsultingServiceCatalogResponse(
            String serviceId,
            String serviceName,
            String description,
            Integer durationMinutes,
            Double basePrice) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.basePrice = basePrice;
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
