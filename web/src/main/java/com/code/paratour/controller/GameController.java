package com.code.paratour.controller;

import java.util.ArrayList;
import java.util.List;

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
            for (Game game : gameService.findAllGames()) {
                if (game.getImage() == null || game.getImage().isBlank()) {
                    game.setImage("https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");
                }
            }

            model.addAttribute("games", gameService.findAllGames());
            return "home";

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    /**
     * Displays the initial form for creating a new game.
     * Default values are only applied if they are not already present (e.g. from redirects).
     */
    @GetMapping("/newGame")
    public String newGameForm(Model model) {
        if (!model.containsAttribute("gameName"))
            model.addAttribute("gameName", "");
        if (!model.containsAttribute("gameDescription"))
            model.addAttribute("gameDescription", "");
        if (!model.containsAttribute("gameType"))
            model.addAttribute("gameType", "");
        if (!model.containsAttribute("gameImage"))
            model.addAttribute("gameImage", "");
        if (!model.containsAttribute("gameVideo"))
            model.addAttribute("gameVideo", "");
        if (!model.containsAttribute("hasLeaderboard"))
            model.addAttribute("hasLeaderboard", "true");
        if (!model.containsAttribute("manual"))
            model.addAttribute("manual", "true");
        if (!model.containsAttribute("message"))
            model.addAttribute("message", "");
        return "newGame";
    }

    /**
     * Deletes a game by ID, including all related phases and enigmas (cascade delete).
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
        List<Phase> phases = phaseService.findByGameId(id);
        for (Phase phase : phases) {
            if (phase.getDescription() == null || phase.getDescription().isBlank()) {
                phase.setDescription("No description available");
            }
            if (phase.getLiteralText() == null || phase.getLiteralText().isBlank()) {
                phase.setLiteralText("Phase " + phase.getPhaseName());
            }

            List<Enigma> enigmas = phase.getEnigmas();
            for (Enigma e : enigmas) {
                if (e.getEnigma() == null || e.getEnigma().isBlank()) {
                    e.setEnigma("No riddle defined yet");
                }
                if (e.getAnswerFormat() == null || e.getAnswerFormat().isBlank()) {
                    e.setAnswerFormat("No format defined");
                }
            }
        }

        model.addAttribute("game", game);
        model.addAttribute("phases", phases);
        return "gameView";
    }

    /**
     * Displays the edit form for a game, including its phases and enigmas.
     * It also reorders the list of game types so that the current type appears first.
     */
    @GetMapping("/editGame/{id}")
    public String editGameForm(@PathVariable Long id, Model model) {
        Game game = gameService.findGameById(id);
        List<Enigma> enigmas = new ArrayList<>();

        int i = 0;
        // Iterate over phases to assign helper IDs and ensure non-null collections
        for (Phase phase : game.getPhases()) {
            phase.setIdFalse(i++);
            int j = 0;
            if (phase.getDescription() == null || phase.getDescription().isBlank()) {
                phase.setDescription("");
            }
            if (phase.getEnigmas() != null) {
                for (Enigma e : phase.getEnigmas()) {
                    e.setIdidTreak(j++); // helper index for the template
                    enigmas.add(e);
                    if (e.getEnigma() == null || e.getEnigma().isBlank()) {
                        e.setEnigma("");
                    }
                    if (e.getAnswerFormat() == null || e.getAnswerFormat().isBlank()) {
                        e.setAnswerFormat("");
                    }
                }
            } else {
                // Guarantee a non-null list even when no enigmas exist
                phase.setEnigmas(new ArrayList<>());
            }
        }

        // Apply default visuals
        if (game.getVideo() == null || game.getVideo().isBlank()) {
            game.setVideo("");
        }
        if (game.getImage() == null || game.getImage().isBlank()) {
            game.setImage("https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");
        }

        // Retrieve all game types and reorder to show the selected one first
        List<GameType> allTypes = typeGameService.findAll();
        List<GameType> orderedTypes = new ArrayList<>();

        String currentType = game.getGameType() == null ? "" : game.getGameType().trim().toLowerCase();

        for (GameType type : allTypes) {
            boolean isSelected = type.getCode().trim().equalsIgnoreCase(currentType)
                    || type.getName().trim().equalsIgnoreCase(currentType);

            type.setIsSelected(isSelected);
            if (isSelected) {
                orderedTypes.add(0, type); // Put the current type first
            } else {
                orderedTypes.add(type);
            }
        }

        model.addAttribute("typesGame", orderedTypes);
        model.addAttribute("enigmas", enigmas);
        model.addAttribute("phases", game.getPhases());
        model.addAttribute("game", game);
        return "editGame";
    }

    /**
     * Updates a game and its related phases and enigmas.
     * Uses index-based iteration to match form data with existing entities.
     */
    @PostMapping("/edit/game/{id}")
    public String updateGame(@PathVariable Long id, @ModelAttribute Game formGame) {
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

        // Synchronize each phase using its list index
        for (int i = 0; i < dbGame.getPhases().size(); i++) {
            Phase formPhase = formGame.getPhases().get(i);
            Phase dbPhase = dbGame.getPhases().get(i); // existing phase with persistent ID

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
                for (int j = 0; j < dbPhase.getEnigmas().size(); j++) {

                    Enigma formEnigma = formPhase.getEnigmas().get(j);
                    Enigma dbEnigma = dbPhase.getEnigmas().get(j); // existing enigma with persistent ID

                    // Update all mutable fields safely
                    dbEnigma.setEnigma(safe(formEnigma.getEnigma()));
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

        // Persist updates (phases and enigmas are cascaded automatically)
        gameService.saveGame(dbGame);
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
