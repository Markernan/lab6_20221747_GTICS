package com.example.lab6.controller;
import com.example.lab6.entity.*;
import com.example.lab6.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private HeroeNavalRepository heroeNavalRepository;

    @Autowired
    private IntencionRepository intencionRepository;


    @Autowired
    private AsignacionCancionRepository asignacionCancionRepository;



    @Autowired
    private CancionCriollaRepository cancionCriollaRepository;





    @Autowired
    private UsuarioRepository usuarioRepository;



    @Autowired
    private NumeroCasaRepository numeroCasaRepository;

    @Autowired
    private SolicitudAsignacionRepository solicitudAsignacionRepository;



    @Autowired
    private MesaRepository mesaRepository;



    @Autowired
    private ReservaRepository reservaRepository;



    @GetMapping("")

    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/heroes")
    public String listarHeroes(Model model) {


        List<HeroeNaval> heroes = heroeNavalRepository.findAll();

        model.addAttribute("heroes", heroes);

        return "admin/heroes/lista";
    }

    @GetMapping("/heroes/nuevo")

    public String nuevoHeroe(Model model) {


        model.addAttribute("heroe", new HeroeNaval());


        return "admin/heroes/formulario";

    }

    @PostMapping("/heroes/guardar")
    public String guardarHeroe(@Valid @ModelAttribute("heroe") HeroeNaval heroe,

                               BindingResult result,
                               RedirectAttributes attr) {

        if (result.hasErrors()) {

            return "admin/heroes/formulario";
        }

        heroeNavalRepository.save(heroe);


        attr.addFlashAttribute("mensaje", "Heroe registrado exitosamente");

        return "redirect:/admin/heroes";

    }

    @GetMapping("/intenciones")


    public String listarIntenciones(Model model) {


        List<Intencion> intenciones = intencionRepository.findAll();


        model.addAttribute("intenciones", intenciones);


        return "admin/intenciones/lista";

    }

    @GetMapping("/asignaciones-cancion")
    public String asignacionesCancion(Model model) {


        List<Usuario> usuarios = usuarioRepository.findAll().stream()
                .filter(u -> "USUARIO".equals(u.getRol().getNombre()))
                .toList();

        List<CancionCriolla> canciones = cancionCriollaRepository.findAll();

        model.addAttribute("usuarios", usuarios);

        model.addAttribute("canciones", canciones);

        return "admin/asignaciones-cancion";
    }

}
