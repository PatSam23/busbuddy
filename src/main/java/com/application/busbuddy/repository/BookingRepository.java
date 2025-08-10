package com.application.busbuddy.repository;

import com.application.busbuddy.model.Booking;
import com.application.busbuddy.model.Schedule;
import com.application.busbuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByScheduleIn(List<Schedule> schedules);
    List<Booking> findByUser(User user);
}
