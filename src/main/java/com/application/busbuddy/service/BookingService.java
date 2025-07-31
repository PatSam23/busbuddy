package com.application.busbuddy.service;

import com.application.busbuddy.dto.request.BookingRequestDTO;
import com.application.busbuddy.dto.response.BookingResponseDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO);
    List<BookingResponseDTO> getBookingsByUserId(Long userId);
    BookingResponseDTO getBookingById(Long bookingId);
    void cancelBooking(Long bookingId);
}
