package com.example.lab6.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "asignaciones_cancion")
public class AsignacionCancion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancion_id", nullable = false)

    private CancionCriolla cancion;
    
    @Column(nullable = false)

    private Integer intentos = 0;
    
    @Column(nullable = false)

    private Boolean adivinada = false;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public CancionCriolla getCancion() {
        return cancion;
    }
    
    public void setCancion(CancionCriolla cancion) {
        this.cancion = cancion;
    }
    
    public Integer getIntentos() {
        return intentos;
    }
    
    public void setIntentos(Integer intentos) {
        this.intentos = intentos;
    }
    
    public Boolean getAdivinada() {
        return adivinada;
    }
    
    public void setAdivinada(Boolean adivinada) {
        this.adivinada = adivinada;
    }
}