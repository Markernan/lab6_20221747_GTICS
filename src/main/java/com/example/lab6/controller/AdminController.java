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

    @PostMapping("/asignaciones-cancion/asignar")
    public String asignarCancion(@RequestParam Long usuarioId,
                                 @RequestParam Long cancionId,
                                 RedirectAttributes attr) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        Optional<CancionCriolla> cancionOpt = cancionCriollaRepository.findById(cancionId);


        if (usuarioOpt.isPresent() && cancionOpt.isPresent()) {

            Usuario usuario = usuarioOpt.get();

            if (asignacionCancionRepository.existePorUsuario(usuario)) {

                attr.addFlashAttribute("error", "El usuario ya tiene una cancion asignada");

                return "redirect:/admin/asignaciones-cancion";
            }

            AsignacionCancion asignacion = new AsignacionCancion();

            asignacion.setUsuario(usuario);

            asignacion.setCancion(cancionOpt.get());


            asignacionCancionRepository.save(asignacion);


            attr.addFlashAttribute("mensaje", "Cancion asignada exitosamente");
        }

        return "redirect:/admin/asignaciones-cancion";

    }

    @GetMapping("/solicitudes")
    public String solicitudes(Model model) {

        List<SolicitudAsignacion> solicitudes = solicitudAsignacionRepository.buscarPorAtendidaFalse();


        model.addAttribute("solicitudes", solicitudes);

        return "admin/solicitudes";

    }

    @PostMapping("/solicitudes/atender")
    public String atenderSolicitud(@RequestParam Long solicitudId,
                                   RedirectAttributes attr) {


        Optional<SolicitudAsignacion> solicitudOpt = solicitudAsignacionRepository.findById(solicitudId);
        if (solicitudOpt.isPresent()) {

            SolicitudAsignacion solicitud = solicitudOpt.get();


            solicitud.setAtendida(true);

            solicitudAsignacionRepository.save(solicitud);

            attr.addFlashAttribute("mensaje", "Solicitud marcada como atendida");


        }

        return "redirect:/admin/solicitudes";
    }

    @GetMapping("/asignaciones-halloween")
    public String asignacionesHalloween(Model model) {

        List<Usuario> usuarios = usuarioRepository.findAll().stream()
                .filter(u -> "USUARIO".equals(u.getRol().getNombre()))
                .toList();
        model.addAttribute("usuarios", usuarios);


        return "admin/asignaciones-halloween";
    }

    @PostMapping("/asignaciones-halloween/asignar")
    public String asignarHalloween(@RequestParam Long usuarioId,

                                   @RequestParam Integer numero,
                                   RedirectAttributes attr) {

        if (numero <= 64) {
            attr.addFlashAttribute("error", "El numero debe ser mayor a 64");
            return "redirect:/admin/asignaciones-halloween";
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (usuarioOpt.isPresent()) {

            Usuario usuario = usuarioOpt.get();

            if (numeroCasaRepository.existePorUsuario(usuario)) {

                attr.addFlashAttribute("error", "El usuario ya tiene un numero asignado");

                return "redirect:/admin/asignaciones-halloween";

            }

            NumeroCasa numeroCasa = new NumeroCasa();


            numeroCasa.setUsuario(usuario);

            numeroCasa.setNumeroObjetivo(numero);


            numeroCasaRepository.save(numeroCasa);

            attr.addFlashAttribute("mensaje", "Numero asignado exitosamente");


        }

        return "redirect:/admin/asignaciones-halloween";


    }

    @GetMapping("/ranking-canciones")
    public String rankingCancionesAdmin(Model model) {

        List<AsignacionCancion> ranking = asignacionCancionRepository.buscarTop10PorAdivinadaTrueOrdenadoPorIntentosAsc();
        if (ranking.size() > 10) {

            ranking = ranking.subList(0, 10);
        }
        model.addAttribute("ranking", ranking);


        return "admin/ranking-canciones";


    }

    @GetMapping("/ranking-halloween")
    public String rankingHalloweenAdmin(Model model) {


        List<NumeroCasa> ranking = numeroCasaRepository.buscarTop10PorAdivinadoTrueOrdenadoPorIntentosAsc();
        if (ranking.size() > 10) {


            ranking = ranking.subList(0, 10);
        }
        model.addAttribute("ranking", ranking);

        return "admin/ranking-halloween";


    }

    @GetMapping("/ver-juegos")
    public String verJuegos(Model model) {

        List<AsignacionCancion> asignacionesCanciones = asignacionCancionRepository.findAll();


        List<NumeroCasa> numerosesCasa = numeroCasaRepository.findAll();


        model.addAttribute("asignacionesCanciones", asignacionesCanciones);
        model.addAttribute("numerosCasa", numerosesCasa);

        return "admin/ver-juegos";
    }

    @GetMapping("/mesas")
    public String administrarMesas(Model model) {
        List<Mesa> mesas = mesaRepository.findAll();
        List<Reserva> reservas = reservaRepository.findAll();

        Long mesasOcupadas = mesaRepository.contarPorDisponibleFalse();
        Long mesasLibres = mesaRepository.contarPorDisponibleTrue();

        model.addAttribute("mesas", mesas);
        model.addAttribute("reservas", reservas);
        model.addAttribute("mesasOcupadas", mesasOcupadas);
        model.addAttribute("mesasLibres", mesasLibres);

        return "admin/mesas";
    }

    @PostMapping("/mesas/liberar")
    public String liberarMesa(@RequestParam Long mesaId,
                              RedirectAttributes attr) {

        Optional<Mesa> mesaOpt = mesaRepository.findById(mesaId);
        if (mesaOpt.isPresent()) {
            Mesa mesa = mesaOpt.get();
            mesa.setDisponible(true);
            mesaRepository.save(mesa);

            List<Reserva> reservas = reservaRepository.findAll().stream()
                    .filter(r -> r.getMesa().getId().equals(mesaId))
                    .toList();

            reservas.forEach(reservaRepository::delete);

            attr.addFlashAttribute("mensaje", "Mesa liberada exitosamente");
        }

        return "redirect:/admin/mesas";
    }

    @PostMapping("/mesas/capacidad")
    public String cambiarCapacidad(@RequestParam Long mesaId,
                                   @RequestParam Integer nuevaCapacidad,
                                   RedirectAttributes attr) {

        if (nuevaCapacidad <= 0 || nuevaCapacidad > 4) {
            attr.addFlashAttribute("error", "La capacidad debe ser entre 1 y 4 personas");

            return "redirect:/admin/mesas";
        }

        Optional<Mesa> mesaOpt = mesaRepository.findById(mesaId);
        if (mesaOpt.isPresent()) {
            Mesa mesa = mesaOpt.get();

            mesa.setCapacidad(nuevaCapacidad);

            mesaRepository.save(mesa);


            attr.addFlashAttribute("mensaje", "Capacidad actualizada exitosamente");
        }

        return "redirect:/admin/mesas";

    }
}
