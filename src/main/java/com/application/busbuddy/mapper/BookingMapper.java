package com.application.busbuddy.mapper;

import com.application.busbuddy.dto.SeatDTO;
import com.application.busbuddy.dto.request.BookingRequestDTO;
import com.application.busbuddy.dto.response.BookingResponseDTO;
import com.application.busbuddy.model.*;
import com.application.busbuddy.model.enums.BookingStatus;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
@Component
public class BookingMapper {

    // Convert Booking -> BookingResponseDTO
    public BookingResponseDTO toDTO(Booking booking) {
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .scheduleId(booking.getSchedule().getId())
                .busName(booking.getSchedule().getBus().getBusNumber())
                .fromLocation(booking.getSchedule().getSource())
                .toLocation(booking.getSchedule().getDestination())
                .departureTime(booking.getSchedule().getDepartureTime())
                .arrivalTime(booking.getSchedule().getArrivalTime())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .seatIds(booking.getSeats().stream().map(Seat::getId).collect(Collectors.toList()))
                .seatNumbers(booking.getSeats().stream().map(Seat::getSeatNumber).collect(Collectors.toList()))
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
