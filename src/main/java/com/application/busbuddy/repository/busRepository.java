package com.application.busbuddy.repository;

import com.application.busbuddy.dto.busInfoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
abstract public class busRepository implements JpaRepository<busInfoDTO, String> {

}
