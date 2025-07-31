package com.application.busbuddy.repository;

import com.application.busbuddy.model.Booking;
import com.application.busbuddy.model.Schedule;
import com.application.busbuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByScheduleIn(List<Schedule> schedules);
}
