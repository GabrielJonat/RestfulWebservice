package com.example.demo.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.UserLog;

public interface UserLogRepository extends JpaRepository<UserLog, Long> {
  
    List<UserLog> findByDataBetween(LocalDateTime startDate, LocalDateTime endDate);
}
