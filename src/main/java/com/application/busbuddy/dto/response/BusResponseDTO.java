package com.application.busbuddy.dto.response;

import com.application.busbuddy.model.enums.BusType;
import lombok.*;

import java.util.List;

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
    private String photo1Url; // New fields
    private String photo2Url;
    private String photo3Url;
    private List<String> allPhotos;
}
