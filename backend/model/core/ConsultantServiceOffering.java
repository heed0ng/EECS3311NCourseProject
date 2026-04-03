package model.core;

import model.user.Consultant;

public class ConsultantServiceOffering {
    private final String offeringId;
    private Consultant consultant;
    private ConsultingService consultingService;
    private Double customPrice;
    private boolean active;

    public ConsultantServiceOffering(String offeringId, Consultant consultant, ConsultingService consultingService, Double customPrice, boolean active) {
        this.offeringId = offeringId;
        this.consultant = consultant;
        this.consultingService = consultingService;
        this.customPrice = customPrice;
        this.active = active;
    }

    public String getOfferingId() { return offeringId; }
    public Consultant getConsultant() { return consultant; }
    public void setConsultant(Consultant consultant) { this.consultant = consultant; }
    public ConsultingService getConsultingService() { return consultingService; }
    public void setConsultingService(ConsultingService consultingService) { this.consultingService = consultingService; }
    public Double getCustomPrice() { return customPrice; }
    public void setCustomPrice(Double customPrice) { this.customPrice = customPrice; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public double getEffectivePrice() {
        return this.customPrice != null ? this.customPrice.doubleValue() : this.consultingService.getBasePrice();
    }

    public int getDurationMinutes() {
        return this.consultingService.getDurationMinutes();
    }
}
