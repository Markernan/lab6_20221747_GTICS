package com.example.lab6.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "canciones_criollas")
public class CancionCriolla {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    
    @Column(nullable = false, length = 150)

    private String titulo;
    
    @Lob
    private String letra;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getLetra() {
        return letra;
    }
    
    public void setLetra(String letra) {
        this.letra = letra;
    }
}