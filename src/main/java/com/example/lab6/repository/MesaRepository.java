package com.example.lab6.repository;

import com.example.lab6.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    @Query("SELECT COUNT(m) FROM Mesa m WHERE m.disponible = false")

    Long contarPorDisponibleFalse();
    
    @Query("SELECT COUNT(m) FROM Mesa m WHERE m.disponible = true")

    Long contarPorDisponibleTrue();
}