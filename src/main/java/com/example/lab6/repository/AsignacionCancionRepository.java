package com.example.lab6.repository;

import com.example.lab6.entity.AsignacionCancion;
import com.example.lab6.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionCancionRepository extends JpaRepository<AsignacionCancion, Long> {
    @Query("SELECT a FROM AsignacionCancion a WHERE a.usuario = :usuario")
    Optional<AsignacionCancion> buscarPorUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AsignacionCancion a WHERE a.usuario = :usuario")
    boolean existePorUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT a FROM AsignacionCancion a WHERE a.adivinada = true ORDER BY a.intentos ASC")
    List<AsignacionCancion> buscarTop10PorAdivinadaTrueOrdenadoPorIntentosAsc();
}