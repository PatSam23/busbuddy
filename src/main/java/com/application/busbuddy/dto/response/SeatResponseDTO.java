package com.application.busbuddy.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatResponseDTO {
    private Long id;
    private String seatNumber;
    private boolean isBooked;
    private double price;
    private Long scheduleId;
}
