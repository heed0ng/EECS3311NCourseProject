package backend.model.core;

import java.time.LocalDateTime;

import backend.model.user.Consultant;

public class AvailabilitySlot {
    private final String slotId;
    private Consultant consultant;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private boolean available;

    public AvailabilitySlot(String slotId, Consultant consultant, LocalDateTime startDateTime, LocalDateTime endDateTime, boolean available) {
        this.slotId = slotId;
        this.consultant = consultant;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.available = available;
    }

    public String getSlotId() { return this.slotId; }
    public Consultant getConsultant() { return this.consultant; }
    public void setConsultant(Consultant consultant) { this.consultant = consultant; }
    public LocalDateTime getStartDateTime() { return this.startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }
    public LocalDateTime getEndDateTime() { return this.endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }
    public boolean isAvailable() { return this.available; }
    public void setAvailable(boolean available) { this.available = available; }

    public boolean isOwnedBy(String consultantId) {
        return this.consultant != null && this.consultant.getUserId().equals(consultantId);
    }

    public boolean overlaps(LocalDateTime start, LocalDateTime end) {
        return this.startDateTime.isBefore(end) && start.isBefore(this.endDateTime);
    }
}
