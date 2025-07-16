package com.application.busbuddy.dto;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class busInfoDTO {
    @Id
    private String busNo;
    @NonNull
    private String busType;
    @NonNull
    private int noOfSeats;
}
