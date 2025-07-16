package com.application.busbuddy.repository;

import com.application.busbuddy.model.Provider;
import com.application.busbuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Optional<Provider> findByEmail(String email);
}

