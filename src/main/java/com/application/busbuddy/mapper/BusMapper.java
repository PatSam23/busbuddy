package com.application.busbuddy.mapper;

import com.application.busbuddy.dto.BusDTO;
import com.application.busbuddy.model.Bus;

public class BusMapper {
    public static BusDTO toDTO(Bus bus) {
        return BusDTO.builder()
                .id(bus.getId())
                .busNumber(bus.getBusNumber())
                .busType(bus.getBusType())
                .totalSeats(bus.getTotalSeats())
                .providerId(bus.getProvider().getId())
                .build();
    }

    public static Bus toEntity(BusDTO dto) {
        return Bus.builder()
                .id(dto.getId())
                .busNumber(dto.getBusNumber())
                .busType(dto.getBusType())
                .totalSeats(dto.getTotalSeats())
                .build(); // set provider separately if needed
    }
}
