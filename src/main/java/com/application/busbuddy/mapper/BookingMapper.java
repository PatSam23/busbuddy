package com.application.busbuddy.mapper;

import com.application.busbuddy.dto.BookingDTO;
import com.application.busbuddy.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public Booking toEntity(BookingDTO dto, User user, Schedule schedule, List<Seat> seats) {
        return Booking.builder()
                .id(dto.getId())
                .user(user)
                .schedule(schedule)
                .status(dto.getStatus())
                .seats(seats)
                .totalAmount(seats.stream()
                        .mapToDouble(Seat::getPrice)
                        .sum())
                .build();
    }

    public BookingDTO toDTO(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .scheduleId(booking.getSchedule().getId())
                .seatIds(booking.getSeats().stream()
                        .map(Seat::getId)
                        .collect(Collectors.toList()))
                .status(booking.getStatus())
                .build();
    }
}
