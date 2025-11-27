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

/**
 * Controller responsible for managing CRUD operations for GameType entities.
 *
 * This includes:
 * - Listing all game types
 * - Creating new game types
 * - Editing existing game types
 * - Deleting game types (with safety checks)
 *
 * GameType entries are used to categorize games and must remain unique,
 * validated by their unique code attribute.
 */
@Controller
@RequestMapping("/gameTypes")
public class GameTypeController {

    @Autowired
    private TypeGameService typeGameService;

    /**
     * Displays the list of all game types.
     * Also initializes an empty GameType object to support the creation form.
     */
    @GetMapping
    public String listGameTypes(Model model) {
        Set<GameType> gameTypes = typeGameService.findAll();
        model.addAttribute("gameTypes", gameTypes);
        model.addAttribute("newGameType", new GameType());
        return "gameTypes";
    }

    /**
     * Saves a new GameType or updates an existing one.
     * Performs validation to ensure that:
     *  - The code field is not empty
     *  - The code is unique when creating new entries
     *
     * Redirects back to the list view with corresponding success/error messages.
     */
    @PostMapping("/save")
    public String saveGameType(@ModelAttribute GameType gameType,
                               RedirectAttributes redirectAttributes) {

        // Basic validation
        if (gameType.getCode() == null || gameType.getCode().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Code cannot be empty.");
            return "redirect:/gameTypes";
        }

        // Prevent creation of duplicated game type codes
        GameType existing = typeGameService.findByCode(gameType.getCode());
        if (existing != null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "A game type with this code already exists.");
            return "redirect:/gameTypes";
        }

        // Save or update
        typeGameService.save(gameType);
        redirectAttributes.addFlashAttribute("successMessage",
                "Game type saved successfully.");
        return "redirect:/gameTypes";
    }

    /**
     * Opens the edit page for the game type identified by its unique code.
     * If the code does not exist, returns to the listing with an error message.
     */
    @GetMapping("/edit/{code}")
    public String editGameType(@PathVariable String code, Model model) {
        GameType gameType = typeGameService.findByCode(code);
        if (gameType == null) {
            model.addAttribute("errorMessage", "Game type not found.");
            return "redirect:/gameTypes";
        }

        model.addAttribute("gameType", gameType);
        return "editGameType";
    }

    /**
     * Deletes a GameType if and only if it has no games associated with it.
     * Safety check prevents deletion of types currently in use.
     *
     * Redirects to the list view with an appropriate warning or success message.
     */
    @PostMapping("/delete/{code}")
    public String deleteGameType(@PathVariable String code,
                                 RedirectAttributes redirectAttributes) {

        int numGames = typeGameService.countGamesByType(code);
        System.out.println("Number of associated games for type " + code + ": " + numGames);

        // Prevent deletion of types in active use
        if (numGames > 0) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "⚠️ This game type cannot be deleted because it has "
                    + numGames + " associated games.");
            return "redirect:/gameTypes";
        }

        typeGameService.deleteByCode(code);
        redirectAttributes.addFlashAttribute("successMessage",
                "Game type deleted successfully.");

        return "redirect:/gameTypes";
    }

    /**
     * Handles the POST request for editing an existing GameType.
     * Updates the record and redirects back to the listing page.
     */
    @PostMapping("/edit/{code}")
    public String editGameType(@ModelAttribute GameType gameType,
                               RedirectAttributes redirectAttributes) {

        if (gameType.getCode() == null || gameType.getCode().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Code cannot be empty.");
            return "redirect:/gameTypes";
        }

        typeGameService.save(gameType);
        redirectAttributes.addFlashAttribute("successMessage",
                "Game type updated successfully.");

        return "redirect:/gameTypes";
    }
}
