package com.application.busbuddy.repository;

import com.application.busbuddy.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScheduleId(Long scheduleId);
    List<Seat> findAllById(Iterable<Long> ids);

    List<Seat> findAllByIdForUpdate(List<Long> requestedSeatIds, Long id);
}
