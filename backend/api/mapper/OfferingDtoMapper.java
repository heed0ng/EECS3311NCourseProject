package backend.api.mapper;

import backend.api.dto.response.OfferingSummaryResponse;
import model.core.ConsultantServiceOffering;

public final class OfferingDtoMapper {

    private OfferingDtoMapper() {
    }


    public static OfferingSummaryResponse toOfferingSummaryResponse(ConsultantServiceOffering offering) {
        return new OfferingSummaryResponse(offering.getOfferingId(), offering.getService().getName(),
                offering.getConsultant().getUserId(), offering.getConsultant().getName(),  
                offering.getService().getDurationMinutes(), offering.getService().getBasePrice(), 
                offering.getService().getDescription());
    }
}