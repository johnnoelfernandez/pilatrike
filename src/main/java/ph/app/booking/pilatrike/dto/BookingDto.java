package ph.app.booking.pilatrike.dto;

import lombok.Data;

@Data
public class BookingDto {
    private Long passengerId;
    private String pickupLocation;
    private String dropoffLocation;
}
