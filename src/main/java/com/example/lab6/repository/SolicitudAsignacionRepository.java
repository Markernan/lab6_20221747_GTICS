package com.example.lab6.repository;

import com.example.lab6.entity.SolicitudAsignacion;
import com.example.lab6.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudAsignacionRepository extends JpaRepository<SolicitudAsignacion, Long> {
    @Query("SELECT s FROM SolicitudAsignacion s WHERE s.atendida = false")
    List<SolicitudAsignacion> buscarPorAtendidaFalse();
    
    @Query("SELECT s FROM SolicitudAsignacion s WHERE s.usuario = :usuario AND s.tipo = :tipo AND s.atendida = false")
    Optional<SolicitudAsignacion> buscarPorUsuarioYTipoYAtendidaFalse(@Param("usuario") Usuario usuario, @Param("tipo") String tipo);
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SolicitudAsignacion s WHERE s.usuario = :usuario AND s.tipo = :tipo AND s.atendida = false")
    boolean existePorUsuarioYTipoYAtendidaFalse(@Param("usuario") Usuario usuario, @Param("tipo") String tipo);
}