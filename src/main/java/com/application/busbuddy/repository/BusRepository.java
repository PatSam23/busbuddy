package com.application.busbuddy.repository;

import com.application.busbuddy.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findByProviderId(Long providerId);

    List<Bus> findAllByProviderId(Long providerId);
}
