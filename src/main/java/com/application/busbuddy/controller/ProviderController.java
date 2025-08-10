package com.application.busbuddy.controller;

import com.application.busbuddy.dto.request.*;
import com.application.busbuddy.dto.response.*;
import com.application.busbuddy.model.Booking;
import com.application.busbuddy.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping("/buses")
    public ResponseEntity<BusResponseDTO> addBus(@Valid @RequestBody BusRequestDTO dto) {
        return ResponseEntity.ok(providerService.addBus(dto));
    }

    @DeleteMapping("/buses/{busId}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long busId) {
        providerService.deleteBus(busId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buses")
    public ResponseEntity<List<BusResponseDTO>> getBuses() {
        return ResponseEntity.ok(providerService.getAllBuses());
    }

    @PostMapping("/schedules")
    public ResponseEntity<ScheduleResponseDTO> addSchedule(@Valid @RequestBody ScheduleRequestDTO dto) {
        return ResponseEntity.ok(providerService.addSchedule(dto));
    }

    @PutMapping("/schedules/{scheduleId}")
    public ResponseEntity<ScheduleResponseDTO> updateSchedule(@PathVariable Long scheduleId, @Valid @RequestBody ScheduleRequestDTO dto) {
        return ResponseEntity.ok(providerService.updateSchedule(scheduleId, dto));
    }

    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        providerService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleResponseDTO>> getSchedules() {
        return ResponseEntity.ok(providerService.getSchedulesForProvider());
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getBookings() {
        return ResponseEntity.ok(providerService.getAllBookingsForProvider());
    }

    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        providerService.cancelBookingByProvider(bookingId);
        return ResponseEntity.noContent().build();
    }
}
