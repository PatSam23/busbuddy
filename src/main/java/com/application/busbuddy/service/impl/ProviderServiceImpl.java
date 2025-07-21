package com.application.busbuddy.service.impl;

import com.application.busbuddy.dto.request.*;
import com.application.busbuddy.dto.response.*;
import com.application.busbuddy.exception.ResourceNotFoundException;
import com.application.busbuddy.mapper.ProviderMapper;
import com.application.busbuddy.model.*;
import com.application.busbuddy.repository.*;
import com.application.busbuddy.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final BusRepository busRepository;
    private final ScheduleRepository scheduleRepository;
    private final BookingRepository bookingRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public ProviderResponseDTO createProvider(ProviderRequestDTO dto) {
        Provider provider = ProviderMapper.toEntity(dto);
        provider.setPassword(passwordEncoder.encode(dto.getPassword()));
        return ProviderMapper.toDTO(providerRepository.save(provider));
    }

    @Override
    public ProviderResponseDTO getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + id));
        return ProviderMapper.toDTO(provider);
    }

    @Override
    public List<ProviderResponseDTO> getAllProviders() {
        return providerRepository.findAll().stream()
                .map(ProviderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProviderResponseDTO updateProvider(Long id, ProviderRequestDTO dto) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + id));

        provider.setName(dto.getName());
        provider.setEmail(dto.getEmail());
        provider.setPassword(passwordEncoder.encode(dto.getPassword()));
        return ProviderMapper.toDTO(providerRepository.save(provider));
    }

    @Override
    public void deleteProvider(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + id));
        providerRepository.delete(provider);
    }
    @Override
    public BusResponseDTO addBus(Long providerId, BusRequestDTO dto) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        Bus bus = Bus.builder()
                .busNumber(dto.getBusNumber())
                .busType(dto.getBusType())
                .totalSeats(dto.getTotalSeats())
                .provider(provider)
                .build();

        return mapBusToDto(busRepository.save(bus));
    }

    @Override
    public void deleteBus(Long providerId, Long busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));

        if (!bus.getProvider().getId().equals(providerId)) {
            throw new RuntimeException("Unauthorized");
        }

        busRepository.delete(bus);
    }

    @Override
    public List<BusResponseDTO> getAllBuses(Long providerId) {
        return busRepository.findAllByProviderId(providerId).stream()
                .map(this::mapBusToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleResponseDTO addSchedule(ScheduleRequestDTO dto) {
        Bus bus = busRepository.findById(dto.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));

        Schedule schedule = Schedule.builder()
                .source(dto.getSource())
                .destination(dto.getDestination())
                .departureTime(dto.getDepartureTime())
                .arrivalTime(dto.getArrivalTime())
                .travelDate(dto.getTravelDate())
                .bus(bus)
                .build();

        return mapScheduleToDto(scheduleRepository.save(schedule));
    }

    @Override
    public ScheduleResponseDTO updateSchedule(Long scheduleId, ScheduleRequestDTO dto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        schedule.setSource(dto.getSource());
        schedule.setDestination(dto.getDestination());
        schedule.setDepartureTime(dto.getDepartureTime());
        schedule.setArrivalTime(dto.getArrivalTime());
        schedule.setTravelDate(dto.getTravelDate());

        return mapScheduleToDto(scheduleRepository.save(schedule));
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }

    @Override
    public List<ScheduleResponseDTO> getSchedulesForProvider(Long providerId) {
        List<Bus> buses = busRepository.findAllByProviderId(providerId);
        return scheduleRepository.findByBusIn(buses).stream()
                .map(this::mapScheduleToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> getAllBookingsForProvider(Long providerId) {
        List<Bus> buses = busRepository.findAllByProviderId(providerId);
        List<Schedule> schedules = (List<Schedule>) scheduleRepository.findByBusIn(buses);
        return bookingRepository.findByScheduleIn(schedules);
    }

    @Override
    public void cancelBookingByProvider(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        bookingRepository.delete(booking);
    }

    private BusResponseDTO mapBusToDto(Bus bus) {
        return BusResponseDTO.builder()
                .id(bus.getId())
                .busNumber(bus.getBusNumber())
                .busType(bus.getBusType())
                .totalSeats(bus.getTotalSeats())
                .build();
    }

    private ScheduleResponseDTO mapScheduleToDto(Schedule schedule) {
        return ScheduleResponseDTO.builder()
                .id(schedule.getId())
                .source(schedule.getSource())
                .destination(schedule.getDestination())
                .departureTime(schedule.getDepartureTime())
                .arrivalTime(schedule.getArrivalTime())
                .travelDate(schedule.getTravelDate())
                .busId(schedule.getBus().getId())
                .build();
    }
}
