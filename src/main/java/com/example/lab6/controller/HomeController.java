package com.example.lab6.controller;

import com.example.lab6.entity.HeroeNaval;
import com.example.lab6.repository.HeroeNavalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private HeroeNavalRepository heroeNavalRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/heroes")
    public String heroes(Model model) {
        List<HeroeNaval> heroes = heroeNavalRepository.findAll();
        model.addAttribute("heroes", heroes);
        return "heroes/lista";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}