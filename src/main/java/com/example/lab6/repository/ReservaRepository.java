package com.example.lab6.repository;

import com.example.lab6.entity.Reserva;
import com.example.lab6.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("SELECT r FROM Reserva r WHERE r.usuario = :usuario")
    Optional<Reserva> buscarPorUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r WHERE r.usuario = :usuario")
    boolean existePorUsuario(@Param("usuario") Usuario usuario);
}