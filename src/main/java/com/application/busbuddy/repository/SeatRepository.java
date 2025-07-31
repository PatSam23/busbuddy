package com.application.busbuddy.repository;

import com.application.busbuddy.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScheduleId(Long scheduleId);

    List<Seat> findAllById(Iterable<Long> ids);
}
