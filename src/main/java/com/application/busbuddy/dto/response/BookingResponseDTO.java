package com.application.busbuddy.dto.response;

import com.application.busbuddy.dto.SeatDTO;
import com.application.busbuddy.model.enums.BookingStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {
    private Long id;
    private Long userId;
    private Long scheduleId;
    private double totalAmount;
    private BookingStatus status;
    private List<SeatDTO> seats;
}
