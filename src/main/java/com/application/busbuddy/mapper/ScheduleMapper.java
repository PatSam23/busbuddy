package com.application.busbuddy.mapper;

import com.application.busbuddy.dto.ScheduleDTO;
import com.application.busbuddy.model.Bus;
import com.application.busbuddy.model.Schedule;

public class ScheduleMapper {

    public static ScheduleDTO toDTO(Schedule schedule) {
        if (schedule == null) return null;
        return ScheduleDTO.builder()
                .id(schedule.getId())
                .source(schedule.getSource())
                .destination(schedule.getDestination())
                .departureTime(schedule.getDepartureTime())
                .arrivalTime(schedule.getArrivalTime())
                .travelDate(schedule.getTravelDate())
                .busId(schedule.getBus().getId())
                .build();
    }

    public static Schedule toEntity(ScheduleDTO dto, Bus bus) {
        if (dto == null || bus == null) return null;
        return Schedule.builder()
                .id(dto.getId())
                .source(dto.getSource())
                .destination(dto.getDestination())
                .departureTime(dto.getDepartureTime())
                .arrivalTime(dto.getArrivalTime())
                .travelDate(dto.getTravelDate())
                .bus(bus)
                .build();
    }
}
