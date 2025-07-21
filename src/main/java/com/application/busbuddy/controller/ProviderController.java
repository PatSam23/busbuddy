package com.application.busbuddy.controller;

import com.application.busbuddy.dto.request.*;
import com.application.busbuddy.dto.response.*;
import com.application.busbuddy.model.Booking;
import com.application.busbuddy.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping("/{providerId}/buses")
    public ResponseEntity<BusResponseDTO> addBus(@PathVariable Long providerId, @RequestBody BusRequestDTO dto) {
        return ResponseEntity.ok(providerService.addBus(providerId, dto));
    }

    @DeleteMapping("/{providerId}/buses/{busId}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long providerId, @PathVariable Long busId) {
        providerService.deleteBus(providerId, busId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{providerId}/buses")
    public ResponseEntity<List<BusResponseDTO>> getBuses(@PathVariable Long providerId) {
        return ResponseEntity.ok(providerService.getAllBuses(providerId));
    }

    @PostMapping("/schedules")
    public ResponseEntity<ScheduleResponseDTO> addSchedule(@RequestBody ScheduleRequestDTO dto) {
        return ResponseEntity.ok(providerService.addSchedule(dto));
    }

    @PutMapping("/schedules/{scheduleId}")
    public ResponseEntity<ScheduleResponseDTO> updateSchedule(@PathVariable Long scheduleId, @RequestBody ScheduleRequestDTO dto) {
        return ResponseEntity.ok(providerService.updateSchedule(scheduleId, dto));
    }

    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        providerService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{providerId}/schedules")
    public ResponseEntity<List<ScheduleResponseDTO>> getSchedules(@PathVariable Long providerId) {
        return ResponseEntity.ok(providerService.getSchedulesForProvider(providerId));
    }

    @GetMapping("/{providerId}/bookings")
    public ResponseEntity<List<Booking>> getBookings(@PathVariable Long providerId) {
        return ResponseEntity.ok(providerService.getAllBookingsForProvider(providerId));
    }

    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        providerService.cancelBookingByProvider(bookingId);
        return ResponseEntity.noContent().build();
    }
}
