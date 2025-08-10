package com.application.busbuddy.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequestDTO {

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be in the future")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required")
    @Future(message = "Arrival time must be in the future")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Travel date is required")
    @FutureOrPresent(message = "Travel Date must be today or in the future")
    private LocalDate travelDate;

    @NotNull(message = "Bus ID is required")
    private Long busId;

    @Positive(message = "Price per seat must be greater than zero")
    private double pricePerSeat;

}
