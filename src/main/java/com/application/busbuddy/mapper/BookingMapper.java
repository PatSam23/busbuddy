package com.application.busbuddy.mapper;

import com.application.busbuddy.dto.SeatDTO;
import com.application.busbuddy.dto.request.BookingRequestDTO;
import com.application.busbuddy.dto.response.BookingResponseDTO;
import com.application.busbuddy.model.*;
import com.application.busbuddy.model.enums.BookingStatus;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    // Convert Booking -> BookingResponseDTO
    public static BookingResponseDTO toBookingResponseDTO(Booking booking) {
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .scheduleId(booking.getSchedule().getId())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .seats(booking.getSeats().stream()
                        .map(seat -> SeatDTO.builder()
                                .id(seat.getId())
                                .seatNumber(seat.getSeatNumber())
                                .scheduleId(seat.getSchedule().getId())
                                .isBooked(seat.isBooked())
                                .price(seat.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // Convert BookingRequestDTO -> Booking
    public static Booking toBooking(BookingRequestDTO dto, User user, Schedule schedule, List<Seat> selectedSeats, double totalAmount) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setSchedule(schedule);
        booking.setSeats(selectedSeats);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalAmount(totalAmount);

        for (Seat seat : selectedSeats) {
            seat.setBooked(true);           // ✅ Mark seat as booked
            seat.setBooking(booking);       // ✅ Set bi-directional booking
        }

        return booking;
    }
}
