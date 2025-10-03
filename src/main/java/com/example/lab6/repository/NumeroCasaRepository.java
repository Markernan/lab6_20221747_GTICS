package com.example.lab6.repository;

import com.example.lab6.entity.NumeroCasa;
import com.example.lab6.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NumeroCasaRepository extends JpaRepository<NumeroCasa, Long> {
    @Query("SELECT n FROM NumeroCasa n WHERE n.usuario = :usuario")
    Optional<NumeroCasa> buscarPorUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM NumeroCasa n WHERE n.usuario = :usuario")
    boolean existePorUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT n FROM NumeroCasa n WHERE n.adivinado = true ORDER BY n.intentos ASC")
    List<NumeroCasa> buscarTop10PorAdivinadoTrueOrdenadoPorIntentosAsc();
}