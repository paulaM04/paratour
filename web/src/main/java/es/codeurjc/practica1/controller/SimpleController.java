package es.codeurjc.practica1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SimpleController {

    @GetMapping("/")
    
    public String home(Model model) {
        
        return "home"; // home.html con Mustache o Thymeleaf
    }

    @GetMapping("/about")
    public String about() {
        return "about"; // página estática sobre la web
    }
}
