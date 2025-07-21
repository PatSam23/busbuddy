package com.application.busbuddy.mapper;

import com.application.busbuddy.dto.request.BusRequestDTO;
import com.application.busbuddy.dto.response.BusResponseDTO;
import com.application.busbuddy.model.Bus;

public class BusMapper {
    public static BusResponseDTO toDTO(Bus bus) {
        return BusResponseDTO.builder()
                .id(bus.getId())
                .busNumber(bus.getBusNumber())
                .busType(bus.getBusType())
                .totalSeats(bus.getTotalSeats())
                .build();
    }

    public static Bus toEntity(BusRequestDTO dto) {
        return Bus.builder()
                .busNumber(dto.getBusNumber())
                .busType(dto.getBusType())
                .totalSeats(dto.getTotalSeats())
                .build(); // set provider separately if needed
    }
}
