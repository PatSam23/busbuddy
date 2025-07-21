package com.application.busbuddy.mapper;

import com.application.busbuddy.dto.SeatDTO;
import com.application.busbuddy.model.Schedule;
import com.application.busbuddy.model.Seat;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    public Seat toEntity(SeatDTO dto, Schedule schedule) {
        return Seat.builder()
                .id(dto.getId())
                .seatNumber(dto.getSeatNumber())
                .schedule(schedule)
                .isBooked(dto.isBooked())
                .price(dto.getPrice())
                .build();
    }

    public SeatDTO toDTO(Seat seat) {
        return SeatDTO.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .scheduleId(seat.getSchedule().getId())
                .isBooked(seat.isBooked())
                .price(seat.getPrice())
                .build();
    }
}
