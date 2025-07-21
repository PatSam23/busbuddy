package com.application.busbuddy.mapper;

import com.application.busbuddy.dto.request.BookingActionDTO;
import com.application.busbuddy.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public Booking toEntity(BookingActionDTO dto, User user, Schedule schedule, List<Seat> seats) {
        return Booking.builder()
                .user(user)
                .schedule(schedule)
                .seats(seats)
                .totalAmount(seats.stream()
                        .mapToDouble(Seat::getPrice)
                        .sum())
                .build();
    }

    public BookingActionDTO toDTO(Booking booking) {
        return BookingActionDTO.builder()
                .build();
    }
}
