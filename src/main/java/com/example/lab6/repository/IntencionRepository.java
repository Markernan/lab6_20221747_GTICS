package com.example.lab6.repository;

import com.example.lab6.entity.Intencion;
import com.example.lab6.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IntencionRepository extends JpaRepository<Intencion, Long> {
    @Query("SELECT i FROM Intencion i WHERE i.usuario = :usuario")
    Optional<Intencion> buscarPorUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Intencion i WHERE i.usuario = :usuario")
    boolean existePorUsuario(@Param("usuario") Usuario usuario);
}