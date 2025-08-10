package com.application.busbuddy.mapper;

import com.application.busbuddy.dto.response.SeatResponseDTO;
import com.application.busbuddy.model.Seat;

public class SeatMapper {
    public static SeatResponseDTO toDTO(Seat seat) {
        return SeatResponseDTO.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .isBooked(seat.isBooked())
                .price(seat.getPrice())
                .scheduleId(seat.getSchedule().getId())
                .build();
    }
}
