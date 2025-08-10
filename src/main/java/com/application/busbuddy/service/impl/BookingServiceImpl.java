package com.application.busbuddy.service.impl;

import com.application.busbuddy.dto.request.BookingRequestDTO;
import com.application.busbuddy.dto.response.BookingResponseDTO;
import com.application.busbuddy.exception.ResourceNotFoundException;
import com.application.busbuddy.mapper.BookingMapper;
import com.application.busbuddy.model.Booking;
import com.application.busbuddy.model.Schedule;
import com.application.busbuddy.model.Seat;
import com.application.busbuddy.model.User;
import com.application.busbuddy.model.enums.BookingStatus;
import com.application.busbuddy.repository.BookingRepository;
import com.application.busbuddy.repository.ScheduleRepository;
import com.application.busbuddy.repository.SeatRepository;
import com.application.busbuddy.repository.UserRepository;
import com.application.busbuddy.service.BookingService;
import com.application.busbuddy.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {
        String username = JwtUtil.getLoggedInUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Schedule schedule = scheduleRepository.findById(bookingRequestDTO.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        // Prefer seatNumbers if provided; fallback to seatIds for backward compatibility
        boolean hasSeatNumbers = bookingRequestDTO.getSeatNumbers() != null && !bookingRequestDTO.getSeatNumbers().isEmpty();
        boolean hasSeatIds = bookingRequestDTO.getSeatIds() != null && !bookingRequestDTO.getSeatIds().isEmpty();

        if (!hasSeatNumbers && !hasSeatIds) {
            throw new IllegalArgumentException("No seats selected. Provide seatNumbers (preferred) or seatIds.");
        }

        List<Seat> seats;
        if (hasSeatNumbers) {
            // Lock seats by (scheduleId, seatNumber) to prevent concurrent booking of the same seats
            // SeatRepository should have a @Lock(PESSIMISTIC_WRITE) query method:
            // List<Seat> findByScheduleIdAndSeatNumberIn(Long scheduleId, Collection<String> seatNumbers);
            List<String> requestedSeatNumbers = bookingRequestDTO.getSeatNumbers();
            seats = seatRepository.findByScheduleIdAndSeatNumberIn(schedule.getId(), requestedSeatNumbers);

            // Validate all requested seatNumbers exist for this schedule
            if (seats.size() != requestedSeatNumbers.size()) {
                throw new ResourceNotFoundException("One or more seatNumbers do not exist for this schedule");
            }

            // Validate seat ownership and availability; check duplicates by seatNumber
            Set<String> seenNumbers = new HashSet<>();
            for (Seat seat : seats) {
                if (!seat.getSchedule().getId().equals(schedule.getId())) {
                    throw new IllegalArgumentException("Seat " + seat.getSeatNumber() + " does not belong to schedule " + schedule.getId());
                }
                if (seat.isBooked() || seat.getBooking() != null) {
                    throw new IllegalStateException("Seat already booked: " + seat.getSeatNumber());
                }
                if (!seenNumbers.add(seat.getSeatNumber())) {
                    throw new IllegalArgumentException("Duplicate seat selected: " + seat.getSeatNumber());
                }
            }
        } else {
            // Legacy flow using seatIds
            List<Long> requestedSeatIds = bookingRequestDTO.getSeatIds();

            // Lock seats to prevent concurrent booking of the same seats (by IDs scoped to schedule)
            seats = seatRepository.findAllByIdForUpdate(requestedSeatIds, schedule.getId());

            // Validate all requested seats exist for this schedule
            if (seats.size() != requestedSeatIds.size()) {
                throw new ResourceNotFoundException("One or more seats do not exist for this schedule");
            }

            // Validate seat ownership and availability; check duplicates by seat ID
            Set<Long> seen = new HashSet<>();
            for (Seat seat : seats) {
                if (!seat.getSchedule().getId().equals(schedule.getId())) {
                    throw new IllegalArgumentException("Seat " + seat.getId() + " does not belong to schedule " + schedule.getId());
                }
                if (seat.isBooked() || seat.getBooking() != null) {
                    throw new IllegalStateException("Seat already booked: " + seat.getSeatNumber());
                }
                if (!seen.add(seat.getId())) {
                    throw new IllegalArgumentException("Duplicate seat selected: " + seat.getSeatNumber());
                }
            }
        }

        double totalAmount = seats.stream().mapToDouble(Seat::getPrice).sum();

        Booking booking = Booking.builder()
                .user(user)
                .schedule(schedule)
                .seats(seats)
                .status(BookingStatus.CONFIRMED)
                .totalAmount(totalAmount)
                .build();

        // Mark seats as booked and set back-reference atomically within the transaction
        seats.forEach(seat -> {
            seat.setBooked(true);
            seat.setBooking(booking);
        });

        return bookingMapper.toDTO(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDTO getBookingById(Long id) {
        String username = JwtUtil.getLoggedInUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        return bookingMapper.toDTO(booking);
    }

    @Override
    public List<BookingResponseDTO> getAllBookingsForUser() {
        String username = JwtUtil.getLoggedInUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByUser(user)
                .stream().map(bookingMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Long id) {
        String username = JwtUtil.getLoggedInUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        // Release seats so they can be booked again
        if (booking.getSeats() != null) {
            booking.getSeats().forEach(seat -> {
                seat.setBooked(false);
                seat.setBooking(null);
            });
        }

        return bookingMapper.toDTO(bookingRepository.save(booking));
    }
}