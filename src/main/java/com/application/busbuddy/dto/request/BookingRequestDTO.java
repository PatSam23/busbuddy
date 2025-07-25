package com.application.busbuddy.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;

    @Min(value = 0, message = "Total amount must be non-negative")
    private double totalAmount;

    @NotNull(message = "Seat IDs are required")
    private List<Long> seatIds;
}
