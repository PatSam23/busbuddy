package com.application.busbuddy.controller;

import com.application.busbuddy.dto.request.BookingRequestDTO;
import com.application.busbuddy.dto.response.BookingResponseDTO;
import com.application.busbuddy.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody BookingRequestDTO dto) {
        log.info("BookingService.createBooking called");
        BookingResponseDTO response = bookingService.createBooking(dto);
        log.info("BookingService.createBooking successfully completed");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingResponseDTO> getBooking(@PathVariable Long id) {
        log.info("BookingService.getBooking called");
        BookingResponseDTO response = bookingService.getBookingById(id);
        log.info("BookingService.getBooking successfully completed");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        log.info("BookingService.getAllBookings called");
        List<BookingResponseDTO> response = bookingService.getAllBookingsForUser();
        log.info("BookingService.getAllBookings successfully completed");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable Long id) {
        log.info("BookingService.cancelBooking called");
        BookingResponseDTO response = bookingService.cancelBooking(id);
        log.info("BookingService.cancelBooking successfully completed");
        return ResponseEntity.ok(response);
    }
}
