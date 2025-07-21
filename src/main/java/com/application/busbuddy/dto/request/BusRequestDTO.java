package com.application.busbuddy.dto.request;

import com.application.busbuddy.model.enums.BusType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusRequestDTO {

    @NotBlank(message = "Bus number is required")
    private String busNumber;

    @NotNull(message = "Bus type is required")
    private BusType busType;

    @NotNull(message = "Total seats is required")
    @Positive(message = "Total seats must be a positive number")
    private Integer totalSeats;
}
