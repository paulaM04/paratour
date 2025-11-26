package com.code.paratour.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.code.paratour.model.GameType;
import com.code.paratour.service.TypeGameService;

@Controller
@RequestMapping("/gameTypes")
public class GameTypeController {

    @Autowired
    private TypeGameService typeGameService;

    @GetMapping
    public String listGameTypes(Model model) {
        Set<GameType> gameTypes = typeGameService.findAll();
        model.addAttribute("gameTypes", gameTypes);
        model.addAttribute("newGameType", new GameType());
        return "gameTypes";
    }

    @PostMapping("/save")
    public String saveGameType(@ModelAttribute GameType gameType,
            RedirectAttributes redirectAttributes) {

        if (gameType.getCode() == null || gameType.getCode().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "El código no puede estar vacío.");
            return "redirect:/gameTypes";
        }

        GameType existing = typeGameService.findByCode(gameType.getCode());

        // Si ya existe un tipo con ese código y no es el mismo objeto (es inserción, no
        // edición)
        if (existing != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ya existe un tipo de juego con ese código.");
            return "redirect:/gameTypes";
        }

        // Si ya existe y estás editando, simplemente guarda (actualiza)
        // Si no existe, guarda (crea)
        typeGameService.save(gameType);

        redirectAttributes.addFlashAttribute("successMessage", "Tipo de juego guardado correctamente.");
        return "redirect:/gameTypes";
    }

    @GetMapping("/edit/{code}")
    public String editGameType(@PathVariable String code, Model model) {
        GameType gameType = typeGameService.findByCode(code);
        if (gameType == null) {
            model.addAttribute("errorMessage", "Tipo de juego no encontrado.");
            return "redirect:/gameTypes";
        }
        model.addAttribute("gameType", gameType);
        return "editGameType";
    }

    @PostMapping("/delete/{code}")
    public String deleteGameType(@PathVariable String code,
            RedirectAttributes redirectAttributes) {

        int numGames = typeGameService.countGamesByType(code);
        System.out.println("Número real de juegos asociados al tipo " + code + ": " + numGames);

        if (numGames > 0) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "⚠️ No se puede eliminar un tipo de juego que tiene " + numGames + " juegos asociados.");
            return "redirect:/gameTypes";
        }

        typeGameService.deleteByCode(code);
        redirectAttributes.addFlashAttribute("successMessage", "Tipo de juego eliminado correctamente.");
        return "redirect:/gameTypes";
    }

    @PostMapping("/edit/{code}")
    public String editGameType(@ModelAttribute GameType gameType,
            RedirectAttributes redirectAttributes) {

        if (gameType.getCode() == null || gameType.getCode().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "El código no puede estar vacío.");
            return "redirect:/gameTypes";
        }

        // Si ya existe y estás editando, simplemente guarda (actualiza)
        // Si no existe, guarda (crea)
        typeGameService.save(gameType);

        redirectAttributes.addFlashAttribute("successMessage", "Tipo de juego guardado correctamente.");
        return "redirect:/gameTypes";
    }
}
