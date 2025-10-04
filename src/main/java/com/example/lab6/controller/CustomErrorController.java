package com.example.lab6.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

        if (statusCode != null) {

            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("error", "no tienes permisos para acceder a esta página");

                model.addAttribute("message", "inicia sesión con las credenciales correctas");
                return "error/403";
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("error", "página no encontrada");

                model.addAttribute("message", "la página que buscas no existe");
                return "error/404";
            }
        }


        model.addAttribute("error", "error interno del servidor");

        model.addAttribute("message", "ha ocurrido un error inesperado");
        return "error/general";
    }
}