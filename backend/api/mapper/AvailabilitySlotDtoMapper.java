package backend.api.mapper;

import backend.api.dto.response.AvailabilitySlotResponse;
// import your real domain class here
import model.core.AvailabilitySlot;

public final class AvailabilitySlotDtoMapper {

    private AvailabilitySlotDtoMapper() {
    }

    public static AvailabilitySlotResponse toAvailabilitySlotResponse(AvailabilitySlot slot) {
        return new AvailabilitySlotResponse( slot.getSlotId(), slot.getOfferingId(), slot.getConsultantId(),
                slot.getStartDateTime().toString(), slot.getEndDateTime().toString(), slot.getStatus().toString());
    }
}