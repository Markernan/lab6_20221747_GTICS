package com.example.lab6.model.entity;
import jakarta.persistence.*;
import org.aspectj.bridge.IMessage;

import java.util.List;
import java.util.Set;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Notblank(message = "El nombre tiene que ser obligatorio")
    @Column(nullable = falsem, length = 100)
    private String nombre;

    @Email(message = "debe ser un correo valido")
    @NotBlank(message = "El correo es obligatorio")
    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @NotBlank(message = "la contraseña es obligatoria")
    @Column(nullable = false, length = 255)
    private String password;
    @ManyToOne(mappedBy = "usuario")
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @OneToMany(mappedBy = "usuario")
    private Set<Intencion> intenciones;

    @OneToMany(mappedBy = "usuario")
    private Set<AsignacionCancion> asignaciesonCancion;

    @OneToMany(mappedBy = "usuario")
    private Set<NumeroCasa> numerosCasa;

    @OneToMany(mappedBy = "usuario")
    private Set<Reserva> reservas;

    public Usuario() {}

    public Usuario(String nombre, String correo, String password, Rol rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
    }
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
    public Set<Intencion> getIntenciones() {
        return intenciones;
    }
    public void setIntenciones(Set<Intencion> intenciones) {
        this.intenciones = intenciones;


}
