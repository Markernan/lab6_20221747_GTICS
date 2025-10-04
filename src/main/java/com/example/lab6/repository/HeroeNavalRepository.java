package com.example.lab6.repository;

import com.example.lab6.entity.HeroeNaval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeroeNavalRepository extends JpaRepository<HeroeNaval, Long> {
}