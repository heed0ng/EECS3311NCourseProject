package backend.model.core;

public class ConsultingService {
    private final String serviceId;
    private String name;
    private String description;
    private int durationMinutes;
    private double basePrice;
    private boolean active;

    public ConsultingService(String serviceId, String name, String description, int durationMinutes, double basePrice, boolean active) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.basePrice = basePrice;
        this.active = active;
    }

    public String getServiceId() { return serviceId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
