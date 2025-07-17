package com.application.busbuddy.dto;

import com.application.busbuddy.model.enums.BusType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusDTO {

    private Long id;

    @NotBlank(message = "Bus number is required")
    private String busNumber;

    @NotNull(message = "Bus type must be specified")
    private BusType busType;

    @Min(value = 1, message = "Total seats must be at least 1")
    private int totalSeats;

    @NotNull(message = "Provider ID must be specified")
    private Long providerId;
}
