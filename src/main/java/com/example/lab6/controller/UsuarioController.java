package com.example.lab6.controller;

import com.example.lab6.entity.*;
import com.example.lab6.repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    
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

    private final List<String> palabrasProhibidas = Arrays.asList("odio", "pelea", "violencia", "malo");

    @GetMapping("/intenciones")
    public String intenciones(Model model, Authentication auth, HttpSession session) {
        String correo = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorCorreo(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Boolean yaEnvio = (Boolean) session.getAttribute("intencionEnviada");
            
            if (yaEnvio == null) {
                yaEnvio = intencionRepository.existePorUsuario(usuario);
                session.setAttribute("intencionEnviada", yaEnvio);
            }
            
            model.addAttribute("yaEnvio", yaEnvio);
            model.addAttribute("intencion", new Intencion());
        }
        
        return "usuario/intenciones";
    }

    @PostMapping("/intenciones/enviar")
    public String enviarIntencion(@Valid @ModelAttribute("intencion") Intencion intencion,
                                 BindingResult result,
                                 Authentication auth,
                                 HttpSession session,
                                 RedirectAttributes attr,
                                 Model model) {
        
        String correo = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorCorreo(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            Boolean yaEnvio = (Boolean) session.getAttribute("intencionEnviada");
            if (yaEnvio == null) {
                yaEnvio = intencionRepository.existePorUsuario(usuario);
            }
            
            if (yaEnvio) {
                attr.addFlashAttribute("error", "Ya has enviado una intencion en esta sesion");
                return "redirect:/usuario/intenciones";
            }
            
            String descripcion = intencion.getDescripcion().toLowerCase();
            boolean tienePalabraProhibida = palabrasProhibidas.stream()
                    .anyMatch(palabra -> descripcion.contains(palabra));
            
            if (tienePalabraProhibida) {
                result.rejectValue("descripcion", "palabraProhibida", "La descripcion contiene palabras no permitidas");
            }
            
            if (result.hasErrors()) {
                model.addAttribute("yaEnvio", false);
                return "usuario/intenciones";
            }
            
            intencion.setUsuario(usuario);
            intencionRepository.save(intencion);
            session.setAttribute("intencionEnviada", true);
            attr.addFlashAttribute("mensaje", "Intencion enviada exitosamente");
        }
        
        return "redirect:/usuario/intenciones";
    }

    @GetMapping("/canciones")
    public String canciones(Model model, Authentication auth, HttpSession session) {
        String correo = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorCorreo(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Optional<AsignacionCancion> asignacionOpt = asignacionCancionRepository.buscarPorUsuario(usuario);
            
            if (asignacionOpt.isPresent()) {
                AsignacionCancion asignacion = asignacionOpt.get();
                model.addAttribute("asignacion", asignacion);
                model.addAttribute("tieneAsignacion", true);
                
                if (asignacion.getAdivinada()) {
                    model.addAttribute("mensaje", "Felicidades! Ya adivinaste la cancion");
                } else {
                    String sessionKey = "progreso_cancion_" + usuario.getId();
                    String progreso = (String) session.getAttribute(sessionKey);
                    model.addAttribute("progreso", progreso);
                    model.addAttribute("intentos", asignacion.getIntentos());
                }
            } else {
                model.addAttribute("tieneAsignacion", false);
                boolean yaSolicito = solicitudAsignacionRepository.existePorUsuarioYTipoYAtendidaFalse(usuario, "CANCION");
                model.addAttribute("yaSolicito", yaSolicito);
            }
        }
        
        return "usuario/canciones";
    }

    @PostMapping("/canciones/adivinar")
    public String adivinarCancion(@RequestParam String respuesta,
                                 Authentication auth,
                                 HttpSession session,
                                 RedirectAttributes attr) {
        
        String correo = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorCorreo(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Optional<AsignacionCancion> asignacionOpt = asignacionCancionRepository.buscarPorUsuario(usuario);
            
            if (asignacionOpt.isPresent()) {
                AsignacionCancion asignacion = asignacionOpt.get();
                String tituloCancion = asignacion.getCancion().getTitulo().toLowerCase();
                String respuestaLimpia = respuesta.toLowerCase().trim();
                
                if (tituloCancion.equals(respuestaLimpia)) {
                    asignacion.setAdivinada(true);
                    asignacion.setIntentos(asignacion.getIntentos() + 1);
                    asignacionCancionRepository.save(asignacion);
                    attr.addFlashAttribute("mensaje", "Felicidades! Adivinaste la cancion en " + asignacion.getIntentos() + " intentos");
                } else {
                    asignacion.setIntentos(asignacion.getIntentos() + 1);
                    asignacionCancionRepository.save(asignacion);
                    
                    String progreso = compararPalabras(tituloCancion, respuestaLimpia);
                    String sessionKey = "progreso_cancion_" + usuario.getId();
                    session.setAttribute(sessionKey, progreso);
                    
                    attr.addFlashAttribute("error", "Respuesta incorrecta. " + progreso);
                }
            }
        }
        
        return "redirect:/usuario/canciones";
    }

    @PostMapping("/canciones/solicitar")
    public String solicitarCancion(Authentication auth, RedirectAttributes attr) {
        String correo = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorCorreo(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            if (!solicitudAsignacionRepository.existePorUsuarioYTipoYAtendidaFalse(usuario, "CANCION")) {
                SolicitudAsignacion solicitud = new SolicitudAsignacion();
                solicitud.setUsuario(usuario);
                solicitud.setTipo("CANCION");
                solicitudAsignacionRepository.save(solicitud);
                attr.addFlashAttribute("mensaje", "Solicitud enviada al administrador");
            } else {
                attr.addFlashAttribute("error", "Ya tienes una solicitud pendiente");
            }
        }
        
        return "redirect:/usuario/canciones";
    }

    @GetMapping("/ranking-canciones")
    public String rankingCanciones(Model model) {
        List<AsignacionCancion> ranking = asignacionCancionRepository.buscarTop10PorAdivinadaTrueOrdenadoPorIntentosAsc();
        if (ranking.size() > 10) {
            ranking = ranking.subList(0, 10);
        }
        model.addAttribute("ranking", ranking);
        return "usuario/ranking-canciones";
    }

    @GetMapping("/halloween")
    public String halloween(Model model, Authentication auth, HttpSession session) {
        String correo = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorCorreo(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Optional<NumeroCasa> numeroCasaOpt = numeroCasaRepository.buscarPorUsuario(usuario);
            
            if (numeroCasaOpt.isPresent()) {
                NumeroCasa numeroCasa = numeroCasaOpt.get();
                model.addAttribute("numeroCasa", numeroCasa);
                model.addAttribute("tieneAsignacion", true);
                
                if (numeroCasa.getAdivinado()) {
                    model.addAttribute("mensaje", "Felicidades! Ya encontraste el camino correcto");
                } else {
                    model.addAttribute("intentos", numeroCasa.getIntentos());
                    model.addAttribute("numeroObjetivo", numeroCasa.getNumeroObjetivo());
                }
            } else {
                model.addAttribute("tieneAsignacion", false);
                boolean yaSolicito = solicitudAsignacionRepository.existePorUsuarioYTipoYAtendidaFalse(usuario, "HALLOWEEN");
                model.addAttribute("yaSolicito", yaSolicito);
            }
        }
        
        return "usuario/halloween";
    }

    @PostMapping("/halloween/adivinar")
    public String adivinarHalloween(@RequestParam Integer pasos,
                                   Authentication auth,
                                   RedirectAttributes attr) {
        
        String correo = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorCorreo(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Optional<NumeroCasa> numeroCasaOpt = numeroCasaRepository.buscarPorUsuario(usuario);
            
            if (numeroCasaOpt.isPresent()) {
                NumeroCasa numeroCasa = numeroCasaOpt.get();
                int pasosMinimos = calcularPasosMinimos(numeroCasa.getNumeroObjetivo());
                
                if (pasos.equals(pasosMinimos)) {
                    numeroCasa.setAdivinado(true);
                    numeroCasa.setIntentos(numeroCasa.getIntentos() + 1);
                    numeroCasaRepository.save(numeroCasa);
                    attr.addFlashAttribute("mensaje", "Felicidades! Encontraste el camino en " + numeroCasa.getIntentos() + " intentos");
                } else {
                    numeroCasa.setIntentos(numeroCasa.getIntentos() + 1);
                    numeroCasaRepository.save(numeroCasa);
                    
                    String pista = pasos > pasosMinimos ? "Tu respuesta es muy alta" : "Tu respuesta es muy baja";
                    attr.addFlashAttribute("error", "Respuesta incorrecta. " + pista + ". Pasos correctos: " + pasosMinimos);
                }
            }
        }
        
        return "redirect:/usuario/halloween";
    }

    @PostMapping("/halloween/solicitar")
    public String solicitarHalloween(Authentication auth, RedirectAttributes attr) {
        String correo = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorCorreo(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            if (!solicitudAsignacionRepository.existePorUsuarioYTipoYAtendidaFalse(usuario, "HALLOWEEN")) {
                SolicitudAsignacion solicitud = new SolicitudAsignacion();
                solicitud.setUsuario(usuario);
                solicitud.setTipo("HALLOWEEN");
                solicitudAsignacionRepository.save(solicitud);
                attr.addFlashAttribute("mensaje", "Solicitud enviada al administrador");
            } else {
                attr.addFlashAttribute("error", "Ya tienes una solicitud pendiente");
            }
        }
        
        return "redirect:/usuario/halloween";
    }

    @GetMapping("/ranking-halloween")
    public String rankingHalloween(Model model) {
        List<NumeroCasa> ranking = numeroCasaRepository.buscarTop10PorAdivinadoTrueOrdenadoPorIntentosAsc();
        if (ranking.size() > 10) {
            ranking = ranking.subList(0, 10);
        }
        model.addAttribute("ranking", ranking);
        return "usuario/ranking-halloween";
    }

    @GetMapping("/mesas")
    public String mesas(Model model, Authentication auth) {
        String correo = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorCorreo(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Optional<Reserva> reservaOpt = reservaRepository.buscarPorUsuario(usuario);
            
            if (reservaOpt.isPresent()) {
                model.addAttribute("tieneReserva", true);
                model.addAttribute("reserva", reservaOpt.get());
            } else {
                model.addAttribute("tieneReserva", false);
                List<Mesa> mesasDisponibles = mesaRepository.findAll().stream()
                        .filter(Mesa::getDisponible)
                        .toList();
                model.addAttribute("mesasDisponibles", mesasDisponibles);
            }
            
            Long mesasOcupadas = mesaRepository.contarPorDisponibleFalse();
            Long mesasLibres = mesaRepository.contarPorDisponibleTrue();
            model.addAttribute("mesasOcupadas", mesasOcupadas);
            model.addAttribute("mesasLibres", mesasLibres);
        }
        
        return "usuario/mesas";
    }

    @PostMapping("/mesas/reservar")
    public String reservarMesa(@RequestParam Long mesaId,
                              Authentication auth,
                              RedirectAttributes attr) {
        
        String correo = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorCorreo(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            if (reservaRepository.existePorUsuario(usuario)) {
                attr.addFlashAttribute("error", "Ya tienes una mesa reservada");
                return "redirect:/usuario/mesas";
            }
            
            Optional<Mesa> mesaOpt = mesaRepository.findById(mesaId);
            if (mesaOpt.isPresent()) {
                Mesa mesa = mesaOpt.get();
                
                if (!mesa.getDisponible()) {
                    attr.addFlashAttribute("error", "La mesa ya no esta disponible");
                    return "redirect:/usuario/mesas";
                }
                
                mesa.setDisponible(false);
                mesaRepository.save(mesa);
                
                Reserva reserva = new Reserva();
                reserva.setUsuario(usuario);
                reserva.setMesa(mesa);
                reservaRepository.save(reserva);
                
                attr.addFlashAttribute("mensaje", "Mesa reservada exitosamente");
            }
        }
        
        return "redirect:/usuario/mesas";
    }

    private String compararPalabras(String original, String respuesta) {
        int letrasCorrectas = 0;
        int posicionesCorrectas = 0;
        
        char[] originalChars = original.toCharArray();
        char[] respuestaChars = respuesta.toCharArray();
        
        int minLength = Math.min(originalChars.length, respuestaChars.length);
        
        for (int i = 0; i < minLength; i++) {
            if (originalChars[i] == respuestaChars[i]) {
                posicionesCorrectas++;
            }
        }
        
        for (char c : respuestaChars) {
            if (original.indexOf(c) != -1) {
                letrasCorrectas++;
            }
        }
        
        return "La cancion tiene " + original.length() + " letras. " +
               "Tienes " + letrasCorrectas + " letras correctas y " +
               posicionesCorrectas + " en posicion correcta.";
    }

    private int calcularPasosMinimos(int numeroObjetivo) {
        if (numeroObjetivo <= 0) return 0;
        
        int[] dp = new int[numeroObjetivo + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        
        for (int i = 1; i <= numeroObjetivo; i++) {
            for (int paso = 1; paso <= 6 && i - paso >= 0; paso++) {
                if (dp[i - paso] != Integer.MAX_VALUE) {
                    dp[i] = Math.min(dp[i], dp[i - paso] + 1);
                }
            }
        }
        
        return dp[numeroObjetivo];
    }
}