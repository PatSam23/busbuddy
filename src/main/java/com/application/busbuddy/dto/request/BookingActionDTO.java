package com.application.busbuddy.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingActionDTO {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;
}
