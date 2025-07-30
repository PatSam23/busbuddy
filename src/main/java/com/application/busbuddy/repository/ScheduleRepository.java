package com.application.busbuddy.repository;

import com.application.busbuddy.model.Bus;
import com.application.busbuddy.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByBusIdAndTravelDate(Long busId, LocalDate travelDate);
    List<Schedule> findByBusId(Long busId);
    List<Schedule> findByBusIn(List<Bus> buses);

}
