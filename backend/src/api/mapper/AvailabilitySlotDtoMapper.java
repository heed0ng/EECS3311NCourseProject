package backend.api.mapper;

import backend.api.dto.response.AvailabilitySlotResponse;
import backend.model.core.AvailabilitySlot;

public final class AvailabilitySlotDtoMapper {

    private AvailabilitySlotDtoMapper() {
    }

    public static AvailabilitySlotResponse toAvailabilitySlotResponse(
            AvailabilitySlot slot,
            String offeringId) {

        return new AvailabilitySlotResponse(
                slot.getSlotId(),
                offeringId,
                slot.getConsultant().getUserId(),
                slot.getStartDateTime().toString(),
                slot.getEndDateTime().toString(),
                slot.isAvailable() ? "AVAILABLE" : "UNAVAILABLE");
    }
}