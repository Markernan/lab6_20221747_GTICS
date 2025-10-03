package com.example.lab6.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "solicitudes_asignacion")
public class SolicitudAsignacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false, length = 20)


    private String tipo;
    
    @Column(nullable = false)


    private Boolean atendida = false;
    
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
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public Boolean getAtendida() {
        return atendida;
    }
    
    public void setAtendida(Boolean atendida) {
        this.atendida = atendida;
    }
}