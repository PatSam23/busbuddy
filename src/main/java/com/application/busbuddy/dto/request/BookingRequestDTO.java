package com.application.busbuddy.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {

    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;

    private List<Long> seatIds;

    private List<String> seatNumbers;
}