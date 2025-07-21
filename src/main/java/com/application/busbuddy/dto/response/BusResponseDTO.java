package com.application.busbuddy.dto.response;

import com.application.busbuddy.model.enums.BusType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusResponseDTO {
    private Long id;
    private String busNumber;
    private BusType busType;
    private int totalSeats;
}
