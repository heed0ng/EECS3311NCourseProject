package backend.api.mapper;

import backend.api.dto.response.BookingSummaryResponse;
// import your real domain class here
import model.core.Booking;

public final class BookingDtoMapper {

    private BookingDtoMapper() {
    }


    public static BookingSummaryResponse toBookingSummaryResponse(Booking booking) {
        return new BookingSummaryResponse(booking.getBookingId(), booking.getClientId(), booking.getOfferingId(),
                booking.getServiceName(),  booking.getConsultantName(), booking.getSlotId(), booking.getStartDateTime().toString(),
                booking.getEndDateTime().toString(), booking.getStateName(), booking.getTotalPrice());
    }

}