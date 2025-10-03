package com.example.lab6.repository;

import com.example.lab6.entity.CancionCriolla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancionCriollaRepository extends JpaRepository<CancionCriolla, Long> {
}