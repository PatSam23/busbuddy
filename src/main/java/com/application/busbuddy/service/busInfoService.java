package com.application.busbuddy.service;

import com.application.busbuddy.dto.busInfoDTO;

public interface busInfoService {
    String fetchBusData(busInfoDTO request);
}
