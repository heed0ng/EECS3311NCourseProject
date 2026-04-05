package backend.api.mapper;

import backend.api.dto.response.BookingSummaryResponse;
import backend.model.core.Booking;

public final class BookingDtoMapper {

    private BookingDtoMapper() {
    }

    public static BookingSummaryResponse toBookingSummaryResponse(Booking booking) {
        return new BookingSummaryResponse(
                booking.getBookingId(),
                booking.getClient().getUserId(),
                booking.getOffering().getOfferingId(),
                booking.getOffering().getConsultingService().getName(),
                booking.getOffering().getConsultant().getName(),
                booking.getSlot().getSlotId(),
                booking.getSlot().getStartDateTime().toString(),
                booking.getSlot().getEndDateTime().toString(),
                booking.getStateName(),
                booking.getPrice());
    }
}