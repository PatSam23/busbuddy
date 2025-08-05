package com.application.busbuddy.dto.response;

import com.application.busbuddy.model.enums.BookingStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {

    private Long id;
    private Long scheduleId;
    private String busName;
    private String fromLocation;
    private String toLocation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BookingStatus status;
    private double totalAmount;
    private List<Long> seatIds;
}
