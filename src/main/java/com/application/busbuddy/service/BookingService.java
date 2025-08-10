package com.application.busbuddy.service;

import com.application.busbuddy.dto.request.BookingRequestDTO;
import com.application.busbuddy.dto.response.BookingResponseDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO);
    BookingResponseDTO getBookingById(Long id);
    List<BookingResponseDTO> getAllBookingsForUser();
    BookingResponseDTO cancelBooking(Long id);
}
