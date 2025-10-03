package com.example.lab6.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "el nombre es obligatorio")
    @Size(max = 100)

    @Column(nullable = false, length = 100)

    private String nombre;
    
    @NotBlank(message = "el correo es obligatorio")


    @Email(message = "formato de correo inválido")

    @Size(max = 100)

    @Column(nullable = false, unique = true, length = 100)

    private String correo;


    @NotBlank(message = "La contraseña es obligatoria")

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caractéres")
    @Column(nullable = false)
    private String password;
    
    @ManyToOne(fetch = FetchType.EAGER)

    @JoinColumn(name = "rol_id", nullable = false)

    private Rol rol;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Rol getRol() {
        return rol;
    }
    
    public void setRol(Rol rol) {
        this.rol = rol;
    }
}