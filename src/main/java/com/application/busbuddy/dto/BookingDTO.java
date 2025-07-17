package com.application.busbuddy.dto;

import com.application.busbuddy.model.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;

    // Changed to List<Long> to handle multiple seats (since Booking → List<Seat>)
    @NotNull(message = "At least one seat ID must be provided")
    private java.util.List<Long> seatIds;

    @NotNull(message = "Booking status must be provided")
    private BookingStatus status;
}
