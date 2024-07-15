package com.example.demo.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Venda;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByDataBetween(LocalDateTime startDate, LocalDateTime endDate);
}
