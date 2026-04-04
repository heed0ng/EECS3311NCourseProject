package backend.api.mapper;

import backend.api.dto.response.OfferingSummaryResponse;
import backend.model.core.ConsultantServiceOffering;

public final class OfferingDtoMapper {

    private OfferingDtoMapper() {
    }

    public static OfferingSummaryResponse toOfferingSummaryResponse(ConsultantServiceOffering offering) {
        return new OfferingSummaryResponse(
                offering.getOfferingId(),
                offering.getConsultingService().getName(),
                offering.getConsultant().getUserId(),
                offering.getConsultant().getName(),
                offering.getDurationMinutes(),
                offering.getEffectivePrice(),
                offering.getConsultingService().getDescription());
    }
}