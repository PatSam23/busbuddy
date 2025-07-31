package com.application.busbuddy.service.impl;

import com.application.busbuddy.dto.SeatDTO;
import com.application.busbuddy.dto.request.BookingRequestDTO;
import com.application.busbuddy.dto.response.BookingResponseDTO;
import com.application.busbuddy.exception.ResourceNotFoundException;
import com.application.busbuddy.mapper.BookingMapper;
import com.application.busbuddy.model.*;
import com.application.busbuddy.model.enums.BookingStatus;
import com.application.busbuddy.repository.*;
import com.application.busbuddy.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {
        // Fetch user and schedule
        User user = userRepository.findById(bookingRequestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + bookingRequestDTO.getUserId()));

        Schedule schedule = scheduleRepository.findById(bookingRequestDTO.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with ID: " + bookingRequestDTO.getScheduleId()));

        // Get seats and validate
        List<Seat> selectedSeats = seatRepository.findAllById(bookingRequestDTO.getSeatIds());

        if (selectedSeats.isEmpty()) {
            throw new ResourceNotFoundException("No seats found with provided IDs.");
        }

        // Check if any seat is already booked
        for (Seat seat : selectedSeats) {
            if (seat.isBooked()) {
                throw new IllegalStateException("Seat already booked: " + seat.getSeatNumber());
            }
        }

        // Calculate total amount
        double totalAmount = selectedSeats.stream().mapToDouble(Seat::getPrice).sum();

        // Set seats as booked
        for (Seat seat : selectedSeats) {
            seat.setBooked(true);
        }

        // Create booking
        Booking booking = BookingMapper.toBooking(bookingRequestDTO, user, schedule, selectedSeats, totalAmount);

        Booking savedBooking = bookingRepository.save(booking);

        // Save updated seat associations
        seatRepository.saveAll(selectedSeats);

        return BookingMapper.toBookingResponseDTO(savedBooking);
    }

    @Override
    public List<BookingResponseDTO> getBookingsByUserId(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream()
                .map(BookingMapper::toBookingResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDTO getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        return BookingMapper.toBookingResponseDTO(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        booking.setStatus(BookingStatus.CANCELLED);

        // Free seats
        for (Seat seat : booking.getSeats()) {
            seat.setBooked(false);
            seat.setBooking(null);
        }

        seatRepository.saveAll(booking.getSeats());
        bookingRepository.save(booking);
    }
}
