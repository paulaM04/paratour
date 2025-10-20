package com.code.paratour.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.code.paratour.model.Enigma;
import com.code.paratour.model.Game;
import com.code.paratour.model.GameType;
import com.code.paratour.model.Phase;
import com.code.paratour.service.EnigmaService;
import com.code.paratour.service.GameService;
import com.code.paratour.service.PhaseService;
import com.code.paratour.service.TypeGameService;

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
    @GetMapping("/deleteGame/{id}")
    public String deleteGame(@PathVariable Long id, Model model) {

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

            return "home";

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
    public String editGameForm(@PathVariable Long id, Model model) {

        // Carga optimizada: trae Game + Phases + Enigmas en UNA sola query
        Game game = gameService.findGameById(id);

        if (game == null) {
            throw new IllegalArgumentException("Game not found with id: " + id);
        }

        // Asignar IDs falsos solo para el front
        int i = 0;
        for (Phase phase : game.getPhases()) {
            phase.setIdFalse(i++);
            int j = 0;

            // Evitar nulls (solo formateo visual)
            if (phase.getDescription() == null)
                phase.setDescription("");
            if (phase.getLatitude() == null)
                phase.setLatitude("");
            if (phase.getLongitude() == null)
                phase.setLongitude("");
            if (phase.getMapUrl() == null)
                phase.setMapUrl("");
            if (phase.getVideo() == null)
                phase.setVideo("");
            if (phase.getImage() == null)
                phase.setImage("");

            // Enigmas ya están cargados (no se dispara ninguna query extra)
            if (phase.getEnigmas() != null) {
                for (Enigma e : phase.getEnigmas()) {
                    e.setIdidTreak(j++);
                    if (e.getStatement() == null)
                        e.setStatement("");
                    if (e.getAnswerFormat() == null)
                        e.setAnswerFormat("");
                }
            } else {
                phase.setEnigmas(new HashSet<>());
            }
        }
        List<Phase> sortedPhases = new ArrayList<>(game.getPhases());
        sortedPhases.sort(Comparator.comparing(Phase::getId));

        for (Phase phase : sortedPhases) {
            List<Enigma> sortedEnigmas = new ArrayList<>(phase.getEnigmas());
            sortedEnigmas.sort(Comparator.comparing(Enigma::getId));
            phase.setEnigmas(new LinkedHashSet<>(sortedEnigmas));
        }
        // Visuales por defecto
        if (game.getImage() == null || game.getImage().isBlank()) {
            game.setImage("https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");
        }
        if (game.getVideo() == null) {
            game.setVideo("");
        }

        // Tipos de juego
        List<GameType> allTypes = new ArrayList<>(typeGameService.findAll());
        List<GameType> orderedTypes = new ArrayList<>();

        String currentType = game.getGameType() == null ? "" : game.getGameType().trim().toLowerCase();

        for (GameType type : allTypes) {
            boolean isSelected = type.getCode().trim().equalsIgnoreCase(currentType)
                    || type.getName().trim().equalsIgnoreCase(currentType);

            type.setIsSelected(isSelected);
            if (isSelected) {
                orderedTypes.add(0, type);
            } else {
                orderedTypes.add(type);
            }
        }

        model.addAttribute("phases", sortedPhases);
        model.addAttribute("game", game);
        model.addAttribute("typesGame", orderedTypes);

        return "editGame";
    }

    /**
     * Updates a game and its related phases and enigmas.
     * Uses index-based iteration to match form data with existing entities.
     */
    @PostMapping("/edit/game/{id}")
    public String updateGame(@PathVariable Long id,
            @ModelAttribute Game formGame,
            RedirectAttributes redirectAttributes,
            Model model,
            @ModelAttribute("newPhase") Phase newPhase) {
        // TO DO
        Game dbGame = gameService.findGameById(id);

        // Update main game attributes
        dbGame.setName(formGame.getName());
        dbGame.setGameType(formGame.getGameType());
        dbGame.setDescription(formGame.getDescription());
        dbGame.setImage(formGame.getImage());
        dbGame.setVideo(formGame.getVideo());
        dbGame.setNumberOfRiddles(formGame.getNumberOfRiddles());
        dbGame.setHasLeaderboard(formGame.isHasLeaderboard());
        dbGame.setManual(formGame.getManual());

        int sizeFormPhases = formGame.getPhases() == null ? 0 : formGame.getPhases().size();
        int sizeDbPhases = dbGame.getPhases() == null ? 0 : dbGame.getPhases().size();
        List<Phase> formPhases = new ArrayList<>(formGame.getPhases());
        List<Phase> dbPhases = new ArrayList<>(dbGame.getPhases());

        // Synchronize each phase using its list index
        for (int i = 0; i < dbGame.getPhases().size(); i++) {
            Phase formPhase = formPhases.get(i);
            Phase dbPhase = dbPhases.get(i); // existing phase with persistent ID

            dbPhase.setPhaseName(formPhase.getPhaseName());
            dbPhase.setDescription(formPhase.getDescription());
            dbPhase.setLatitude(formPhase.getLatitude());
            dbPhase.setLongitude(formPhase.getLongitude());
            dbPhase.setMapUrl(formPhase.getMapUrl());
            dbPhase.setGame(dbGame);

            if (dbPhase.getLiteralText() == null || dbPhase.getLiteralText().isBlank()) {
                dbPhase.setLiteralText("Phase " + dbPhase.getPhaseName());
            }

            // Synchronize enigmas (riddles) for each phase
            if (formPhase.getEnigmas() != null) {
                List<Enigma> formEnigmas = new ArrayList<>(formPhase.getEnigmas());
                List<Enigma> dbEnigmas = new ArrayList<>(dbPhase.getEnigmas());
                for (int j = 0; j < dbPhase.getEnigmas().size(); j++) {

                    Enigma formEnigma = formEnigmas.get(j);
                    Enigma dbEnigma = dbEnigmas.get(j); // existing enigma with persistent ID

                    // Update all mutable fields safely
                    dbEnigma.setStatement(safe(formEnigma.getStatement()));
                    dbEnigma.setAnswer(safe(formEnigma.getAnswer()));
                    dbEnigma.setHint1(safe(formEnigma.getHint1()));
                    dbEnigma.setHint2(safe(formEnigma.getHint2()));
                    dbEnigma.setAnswerFormat(safe(formEnigma.getAnswerFormat()));
                    dbEnigma.setPointsCorrect(safeInt(formEnigma.getPointsCorrect()));
                    dbEnigma.setPointsFail(safeInt(formEnigma.getPointsFail()));
                    dbEnigma.setPointsHint1(safeInt(formEnigma.getPointsHint1()));
                    dbEnigma.setPointsHint2(safeInt(formEnigma.getPointsHint2()));
                    dbEnigma.setImage(safe(formEnigma.getImage()));
                    dbEnigma.setLocation(safe(formEnigma.getLocation()));
                    dbEnigma.setIntroduction(safe(formEnigma.getIntroduction()));
                    dbEnigma.setIntroAvatarVideo(safe(formEnigma.getIntroAvatarVideo()));
                    dbEnigma.setEnigmaVideo(safe(formEnigma.getEnigmaVideo()));
                    dbEnigma.setExplanationSpot(safe(formEnigma.getExplanationSpot()));
                    dbEnigma.setExplanationSpotVideo(safe(formEnigma.getExplanationSpotVideo()));
                    dbEnigma.setLocationResolutionPhoto(safe(formEnigma.getLocationResolutionPhoto()));
                    dbEnigma.setMaxTime(safeInt(formEnigma.getMaxTime()));
                    dbEnigma.setLatitude(safe(formEnigma.getLatitude()));
                    dbEnigma.setLongitude(safe(formEnigma.getLongitude()));
                    dbEnigma.setAdditionalInstructions(safe(formEnigma.getAdditionalInstructions()));
                    dbEnigma.setManual(safeBool(formEnigma.getManual()));

                    dbEnigma.setPhase(dbPhase);
                }
            }
        }
        if (sizeFormPhases != sizeDbPhases) {
            dbGame.addPhase(newPhase);
        }

        // Persist updates (phases and enigmas are cascaded automatically)
        gameService.saveGame(dbGame);
        redirectAttributes.addFlashAttribute("successMessage", "✅ El juego se ha guardado correctamente.");
        model.addAttribute("phases", dbGame.getPhases());

        return "redirect:/editGame/" + id + "?success=1";
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
