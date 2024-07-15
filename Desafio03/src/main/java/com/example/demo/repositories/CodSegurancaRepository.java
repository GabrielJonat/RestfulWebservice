package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.CodigosDeSeguranca;

@Repository
public interface CodSegurancaRepository extends CrudRepository<CodigosDeSeguranca, Integer> {
    CodigosDeSeguranca findByCode(String code);
}