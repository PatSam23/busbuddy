package com.application.busbuddy.service.impl;

import com.application.busbuddy.config.JwtUtil;
import com.application.busbuddy.dto.request.*;
import com.application.busbuddy.dto.response.*;
import com.application.busbuddy.exception.ResourceNotFoundException;
import com.application.busbuddy.mapper.ProviderMapper;
import com.application.busbuddy.mapper.ScheduleMapper;
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

    private Provider getLoggedInProvider() {
        String email = JwtUtil.getLoggedInUsername();
        return providerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in provider not found"));
    }

    // -------------------- PROVIDER CRUD --------------------

    @Override
    public ProviderResponseDTO createProvider(ProviderRequestDTO dto) {
        Provider provider = ProviderMapper.toEntity(dto);
        provider.setPassword(passwordEncoder.encode(dto.getPassword()));
        return ProviderMapper.toDTO(providerRepository.save(provider));
    }

    @Override
    public ProviderResponseDTO getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + id));
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
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + id));

        provider.setName(dto.getName());
        provider.setEmail(dto.getEmail());
        provider.setPassword(passwordEncoder.encode(dto.getPassword()));

        return ProviderMapper.toDTO(providerRepository.save(provider));
    }

    @Override
    public void deleteProvider(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + id));
        providerRepository.delete(provider);
    }

    // -------------------- BUS MANAGEMENT --------------------

    @Override
    public BusResponseDTO addBus(BusRequestDTO dto) {
        Provider provider = getLoggedInProvider();

        Bus bus = Bus.builder()
                .busNumber(dto.getBusNumber())
                .busType(dto.getBusType())
                .totalSeats(dto.getTotalSeats())
                .provider(provider)
                .build();

        return mapBusToDto(busRepository.save(bus));
    }

    @Override
    public void deleteBus(Long busId) {
        Provider provider = getLoggedInProvider();
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + busId));

        if (!bus.getProvider().getId().equals(provider.getId())) {
            throw new RuntimeException("Unauthorized deletion attempt");
        }

        busRepository.delete(bus);
    }

    @Override
    public List<BusResponseDTO> getAllBuses() {
        Provider provider = getLoggedInProvider();
        return busRepository.findAllByProviderId(provider.getId()).stream()
                .map(this::mapBusToDto)
                .collect(Collectors.toList());
    }

    // -------------------- SCHEDULE MANAGEMENT --------------------

    @Override
    public ScheduleResponseDTO addSchedule(ScheduleRequestDTO dto) {
        Provider provider = getLoggedInProvider();
        Bus bus = busRepository.findById(dto.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + dto.getBusId()));

        if (!bus.getProvider().getId().equals(provider.getId())) {
            throw new RuntimeException("Unauthorized to add schedule for this bus");
        }

        List<Schedule> existingSchedules = scheduleRepository.findByBusId(dto.getBusId());
        for (Schedule existing : existingSchedules) {
            boolean overlaps =
                    !dto.getArrivalTime().isBefore(existing.getDepartureTime()) &&
                            !dto.getDepartureTime().isAfter(existing.getArrivalTime()) &&
                            dto.getTravelDate().equals(existing.getTravelDate());
            if (overlaps) {
                throw new RuntimeException("Overlapping schedule exists for this bus on the same date");
            }
        }

        Schedule schedule = ScheduleMapper.toEntity(dto, bus);
        Schedule saved = scheduleRepository.save(schedule);
        return ScheduleMapper.toDTO(saved);
    }

    @Override
    public ScheduleResponseDTO updateSchedule(Long scheduleId, ScheduleRequestDTO dto) {
        Provider provider = getLoggedInProvider();
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        if (!schedule.getBus().getProvider().getId().equals(provider.getId())) {
            throw new RuntimeException("Unauthorized to update this schedule");
        }

        List<Schedule> existingSchedules = scheduleRepository.findByBusId(dto.getBusId());
        for (Schedule existing : existingSchedules) {
            if (!existing.getId().equals(scheduleId)) {
                boolean overlaps =
                        !dto.getArrivalTime().isBefore(existing.getDepartureTime()) &&
                                !dto.getDepartureTime().isAfter(existing.getArrivalTime()) &&
                                dto.getTravelDate().equals(existing.getTravelDate());
                if (overlaps) {
                    throw new RuntimeException("Updated schedule overlaps with another schedule for this bus");
                }
            }
        }

        schedule.setSource(dto.getSource());
        schedule.setDestination(dto.getDestination());
        schedule.setDepartureTime(dto.getDepartureTime());
        schedule.setArrivalTime(dto.getArrivalTime());
        schedule.setTravelDate(dto.getTravelDate());

        return ScheduleMapper.toDTO(scheduleRepository.save(schedule));
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        Provider provider = getLoggedInProvider();
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        if (!schedule.getBus().getProvider().getId().equals(provider.getId())) {
            throw new RuntimeException("Unauthorized to delete this schedule");
        }

        scheduleRepository.delete(schedule);
    }

    @Override
    public List<ScheduleResponseDTO> getSchedulesForProvider() {
        Provider provider = getLoggedInProvider();
        List<Bus> buses = busRepository.findAllByProviderId(provider.getId());
        return scheduleRepository.findByBusIn(buses).stream()
                .map(this::mapScheduleToDto)
                .collect(Collectors.toList());
    }

    // -------------------- BOOKING VIEW & CANCEL --------------------

    @Override
    public List<Booking> getAllBookingsForProvider() {
        Provider provider = getLoggedInProvider();
        List<Bus> buses = busRepository.findAllByProviderId(provider.getId());
        List<Schedule> schedules = scheduleRepository.findByBusIn(buses);
        return bookingRepository.findByScheduleIn(schedules);
    }

    @Override
    public void cancelBookingByProvider(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        bookingRepository.delete(booking);
    }

    // -------------------- MAPPING HELPERS --------------------

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
                .pricePerSeat(schedule.getSeats() != null && !schedule.getSeats().isEmpty()
                        ? schedule.getSeats().get(0).getPrice()
                        : 0.0)
                .build();
    }
}
