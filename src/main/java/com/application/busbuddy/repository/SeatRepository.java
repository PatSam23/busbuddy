package com.application.busbuddy.repository;

import com.application.busbuddy.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScheduleId(Long scheduleId);
    List<Seat> findAllById(Iterable<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.id in :ids and s.schedule.id = :scheduleId")
    List<Seat> findAllByIdForUpdate(@Param("ids") List<Long> ids, @Param("scheduleId") Long scheduleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Seat> findByScheduleIdAndSeatNumberIn(Long scheduleId, Collection<String> seatNumbers);
}