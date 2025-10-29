package com.code.paratour.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.code.paratour.model.Enigma;
import com.code.paratour.model.Game;
import com.code.paratour.model.Phase;
import com.code.paratour.service.EnigmaService;
import com.code.paratour.service.GameService;
import com.code.paratour.service.PhaseService;
import com.code.paratour.service.TypeGameService;

import jakarta.transaction.Transactional;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private PhaseService phaseService;

    @Autowired
    private EnigmaService enigmaService;

    @Autowired
    private TypeGameService typeGameService;

    /**
     * Displays the home page with the list of all games.
     * It also ensures that every game has a default image if none is set.
     */
    @GetMapping("/")
    public String home(Model model) {
        try {
            // Ensure that each game has a valid preview image
            // for (Game game : gameService.findAllGames()) {
            // if (game.getImage() == null || game.getImage().isBlank()) {
            // game.setImage(
            // "https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");
            // }
            // }
            model.addAttribute("games", gameService.findAllGames());
            return "home";

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    /**
     * Deletes a game by ID, including all related phases and enigmas (cascade
     * delete).
     */
    @Transactional
    @GetMapping("/deleteGame/{id}")
    public String deleteGame(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

        try {
            Game game = gameService.findGameById(id);

            if (game == null) {
                model.addAttribute("message", "Game with ID " + id + " does not exist.");
                return "error";
            }

            // First, delete all enigmas linked to each phase
            for (Phase phase : game.getPhases()) {
                if (phase.getEnigmas() != null) {
                    for (Enigma enigma : phase.getEnigmas()) {
                        enigmaService.delete(enigma.getId());
                    }
                }
            }

            // Then, delete all phases associated with the game
            for (Phase phase : game.getPhases()) {
                phaseService.delete(phase.getId());
            }

            // Finally, delete the game itself
            gameService.deleteGame(id);

            model.addAttribute("games", gameService.findAllGames());
            model.addAttribute("successMessage", "Game deleted successfully.");
            redirectAttributes.addFlashAttribute("successMessage", "✅ El juego se ha borrado correctamente.");

            return "redirect:/";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Error while deleting the game: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Displays a specific game with all its phases and enigmas.
     * Default placeholders are applied for missing data.
     */
    @GetMapping("/games/{id}")
    public String viewGame(@PathVariable("id") Long id, Model model) {

        Game game = gameService.findGameById(id);
        if (game == null) {
            throw new IllegalArgumentException("Game not found with id: " + id);
        }

        // Apply default placeholders for empty or null fields
        if (game.getImage() == null || game.getImage().isBlank()) {
            game.setImage("https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");
        }
        if (game.getVideo() == null || game.getVideo().isBlank()) {
            game.setVideo("");
        }
        if (game.getDescription() == null || game.getDescription().isBlank()) {
            game.setDescription("No description available");
        }

        // Load and prepare associated phases and enigmas
        Set<Phase> phases = game.getPhases();
        for (Phase phase : phases) {
            if (phase.getDescription() == null || phase.getDescription().isBlank()) {
                phase.setDescription("No description available");
            }
            if (phase.getLiteralText() == null || phase.getLiteralText().isBlank()) {
                phase.setLiteralText("Phase " + phase.getPhaseName());
            }
            System.out.println("El id de la FASE es: " + phase.getId());
            Set<Enigma> enigmas = phase.getEnigmas();
            for (Enigma e : enigmas) {
                if (e.getStatement() == null || e.getStatement().isBlank()) {
                    e.setStatement("No riddle defined yet");
                }
                if (e.getAnswerFormat() == null || e.getAnswerFormat().isBlank()) {
                    e.setAnswerFormat("No format defined");
                }
            }
        }
        List<Phase> sortedPhases = new ArrayList<>(game.getPhases());
        sortedPhases.sort(Comparator.comparing(Phase::getId));

        for (Phase phase : sortedPhases) {
            List<Enigma> sortedEnigmas = new ArrayList<>(phase.getEnigmas());
            sortedEnigmas.sort(Comparator.comparing(Enigma::getId));
            phase.setEnigmas(new LinkedHashSet<>(sortedEnigmas));
        }

        model.addAttribute("phases", sortedPhases);
        model.addAttribute("game", game);
        return "gameView";
    }

    /**
     * Displays the edit form for a game, including its phases and enigmas.
     * It also reorders the list of game types so that the current type appears
     * first.
     */
    @GetMapping("/editGame/{id}")
    public String editGame(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Game dbGame = gameService.findGameById(id);
        if (dbGame == null) {
            ra.addFlashAttribute("errorMessage", "❌ Juego no encontrado.");
            return "redirect:/";
        }

        // Pasamos el juego tal cual para lecturas ({{game.*}})
        model.addAttribute("game", dbGame);

        // Construimos filas con índice estable
        List<Phase> ordered = new ArrayList<>(dbGame.getPhases());
        ordered.sort(Comparator.comparing(Phase::getId)); // orden determinista

        // phaseRows = List<Map<String,Object>> con { idx, phase }
        List<Map<String, Object>> phaseRows = new ArrayList<>();
        for (int i = 0; i < ordered.size(); i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("idx", i);
            row.put("phase", ordered.get(i));
            phaseRows.add(row);
        }
        model.addAttribute("phaseRows", phaseRows);

        return "editGame";
    }

    @PostMapping("/edit/game/{id}")
    public String updateGame(
            @PathVariable Long id,
            @ModelAttribute("game") Game formGame,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        // aquí llamamos al servicio
        this.saveGameController(formGame);

        redirectAttributes.addFlashAttribute("successMessage", "✅ El juego se ha guardado correctamente.");
        return "redirect:/editGame/" + id + "?success=1";
    }

    @Transactional
    public Game saveGameController(Game game) {
        // Si el juego ya existe, lo recuperamos de la BD
        Game dbGame = gameService.findGameById(game.getId());
        // === Actualizar datos principales ===
        dbGame.setNumberOfRiddles(game.getPhases().size());
        
        dbGame.setName(game.getName());
        dbGame.setDescription(game.getDescription());
        dbGame.setImage(game.getImage());
        dbGame.setVideo(game.getVideo());
        dbGame.setGameType(game.getGameType());
        dbGame.setHasLeaderboard(game.isHasLeaderboard());
        dbGame.setManual(game.getManual());
        dbGame.setNumberOfRiddles(game.getNumberOfRiddles());

        // === Actualizar o añadir fases ===
        if (game.getPhases() != null) {
            // Guardamos las IDs existentes para evitar duplicados
            Set<Long> existingIds = dbGame.getPhases().stream()
                    .map(Phase::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            for (Phase formPhase : game.getPhases()) {
                if (formPhase.getId() != null && existingIds.contains(formPhase.getId())) {
                    // Fase existente -> actualizar campos
                    Phase dbPhase = dbGame.getPhases().stream()
                            .filter(p -> Objects.equals(p.getId(), formPhase.getId()))
                            .findFirst()
                            .orElseThrow();
                    dbPhase.setPhaseName(formPhase.getPhaseName());
                    dbPhase.setDescription(formPhase.getDescription());
                    dbPhase.setLatitude(formPhase.getLatitude());
                    dbPhase.setLongitude(formPhase.getLongitude());
                    dbPhase.setMapUrl(formPhase.getMapUrl());
                    dbPhase.setVideo(formPhase.getVideo());
                    dbPhase.setImage(formPhase.getImage());
                } else {
                    // Fase nueva -> asociar y añadir
                    formPhase.setId(null); // por si acaso
                    formPhase.setGame(dbGame);
                    dbGame.getPhases().add(formPhase);
                }
            }
        }

        // === Asegurar relaciones ===
        for (Phase phase : dbGame.getPhases()) {
            phase.setGame(dbGame);
        }

        // === Guardar todo ===
        return gameService.saveGame(dbGame);
    }

    @InitBinder("game")
    public void initBinder(WebDataBinder binder) {
        // Excluye el binding automático del campo 'phases'
        binder.setDisallowedFields("phases");
    }

    /**
     * Utility methods for safely handling null values.
     */
    public String safe(String value) {
        return (value == null) ? "" : value;
    }

    public Integer safeInt(Integer value) {
        return (value == null) ? 0 : value;
    }

    public Boolean safeBool(Boolean value) {
        return (value == null) ? true : value;
    }

    /**
     * Global exception handler for missing request parameters.
     * Provides user-friendly error messages instead of stack traces.
     */
    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public String handleMissingParams(MissingServletRequestParameterException ex, Model model) {
            model.addAttribute("message", "Missing required parameter: " + ex.getParameterName());
            return "error";
        }
    }

}
