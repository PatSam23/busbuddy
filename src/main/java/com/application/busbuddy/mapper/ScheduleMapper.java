package com.application.busbuddy.mapper;

import com.application.busbuddy.dto.request.ScheduleRequestDTO;
import com.application.busbuddy.dto.response.ScheduleResponseDTO;
import com.application.busbuddy.model.Bus;
import com.application.busbuddy.model.Schedule;
import com.application.busbuddy.model.Seat;

import java.util.ArrayList;
import java.util.List;

public class ScheduleMapper {

    public static ScheduleResponseDTO toDTO(Schedule schedule) {
        if (schedule == null) return null;
        return ScheduleResponseDTO.builder()
                .id(schedule.getId())
                .source(schedule.getSource())
                .destination(schedule.getDestination())
                .departureTime(schedule.getDepartureTime())
                .arrivalTime(schedule.getArrivalTime())
                .travelDate(schedule.getTravelDate())
                .busId(schedule.getBus().getId())
                .pricePerSeat(getPricePerSeat(schedule))
                .build();
    }

    public static Schedule toEntity(ScheduleRequestDTO dto, Bus bus) {
        if (dto == null || bus == null) return null;

        Schedule schedule = Schedule.builder()
                .source(dto.getSource())
                .destination(dto.getDestination())
                .departureTime(dto.getDepartureTime())
                .arrivalTime(dto.getArrivalTime())
                .travelDate(dto.getTravelDate())
                .bus(bus)
                .build();

        // Create seats for each seat in the bus
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= bus.getTotalSeats(); i++) {
            Seat seat = Seat.builder()
                    .seatNumber("Seat_" + i)
                    .price(dto.getPricePerSeat())
                    .isBooked(false)
                    .schedule(schedule)
                    .build();
            seats.add(seat);
        }

        schedule.setSeats(seats);
        return schedule;
    }

    private static double getPricePerSeat(Schedule schedule) {
        if (schedule.getSeats() == null || schedule.getSeats().isEmpty()) return 0.0;
        return schedule.getSeats().get(0).getPrice(); // Uniform pricing assumption
    }
}
