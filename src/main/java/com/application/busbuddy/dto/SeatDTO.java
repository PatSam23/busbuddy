package com.application.busbuddy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatDTO {

    private Long id;

    @NotBlank(message = "Seat number is required")
    private String seatNumber;

    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;

    private boolean isBooked;

    @Positive(message = "Price must be greater than zero")
    private double price;
}
