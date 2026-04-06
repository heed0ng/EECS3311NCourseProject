package backend.api.dto.request;

public class CreateServiceOfferingRequest {

    private String serviceId;
    private Double customPrice;

    public CreateServiceOfferingRequest() {
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Double getCustomPrice() {
        return this.customPrice;
    }

    public void setCustomPrice(Double customPrice) {
        this.customPrice = customPrice;
    }
}
