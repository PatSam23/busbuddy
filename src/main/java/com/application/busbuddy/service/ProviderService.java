package com.application.busbuddy.service;

import com.application.busbuddy.dto.request.*;
import com.application.busbuddy.dto.response.*;
import com.application.busbuddy.model.Booking;

import java.util.List;

public interface ProviderService {
    ProviderResponseDTO createProvider(ProviderRequestDTO dto);
    ProviderResponseDTO getProviderById(Long id);
    List<ProviderResponseDTO> getAllProviders();
    ProviderResponseDTO updateProvider(Long id, ProviderRequestDTO dto);
    void deleteProvider(Long id);

    BusResponseDTO addBus(BusRequestDTO dto);
    void deleteBus(Long busId);
    List<BusResponseDTO> getAllBuses();

    ScheduleResponseDTO addSchedule(ScheduleRequestDTO dto);
    ScheduleResponseDTO updateSchedule(Long scheduleId, ScheduleRequestDTO dto);
    void deleteSchedule(Long scheduleId);
    List<ScheduleResponseDTO> getSchedulesForProvider();

    List<Booking> getAllBookingsForProvider();
    void cancelBookingByProvider(Long bookingId);
}
