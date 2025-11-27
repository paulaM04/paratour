package com.code.paratour.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.code.paratour.model.Enigma;
import com.code.paratour.model.Game;
import com.code.paratour.model.GameType;
import com.code.paratour.model.Phase;
import com.code.paratour.service.EnigmaService;
import com.code.paratour.service.GameService;
import com.code.paratour.service.PhaseService;
import com.code.paratour.service.TypeGameService;

import jakarta.transaction.Transactional;

/**
 * Controller responsible for managing all game-related operations,
 * including listing games, viewing details, editing, deleting, and
 * updating both phases and enigmas.
 *
 * This controller orchestrates interactions between Game, Phase,
 * Enigma and GameType entities within a multi-step editing flow.
 */
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
     * Displays the home page with a list of all available games.
     * If any game is missing an image, a default placeholder is used.
     */
    @GetMapping("/")
    public String home(Model model) {
        try {
            model.addAttribute("games", gameService.findAllGames());
            return "home";

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    /**
     * Deletes a game by ID, including all related phases and enigmas.
     * Deletion is executed manually to ensure consistency and avoid
     * orphaned references, despite cascade configuration.
     *
     * The method:
     *  1. Removes all enigmas inside the game's phases  
     *  2. Deletes all phases  
     *  3. Deletes the game itself  
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

            // Decrement the number of games associated with this type
            GameType type = typeGameService.findByCode(game.getGameType());
            type.setNumGames(type.getNumGames() - 1);

            // Delete all enigmas inside all phases
            for (Phase phase : game.getPhases()) {
                if (phase.getEnigmas() != null) {
                    Set<Enigma> enigmas = phase.getEnigmas();
                    for (Enigma enigma : enigmas) {
                        phase.getEnigmas().remove(enigma);
                        enigmaService.delete(enigma.getId());
                    }
                }
            }

            // Delete each phase after its enigmas are removed
            for (Phase phase : game.getPhases()) {
                phaseService.delete(phase.getId());
            }

            // Finally, delete the game itself
            gameService.deleteGame(id);

            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Game deleted successfully.");

            return "redirect:/";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Error while deleting the game: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Displays the detailed view of a single game, including its phases
     * and all associated enigmas.
     *
     * Default placeholder values are applied for missing or empty fields,
     * ensuring the UI always displays clean information.
     */
    @GetMapping("/games/{id}")
    public String viewGame(@PathVariable("id") Long id, Model model) {

        Game game = gameService.findGameById(id);
        if (game == null) {
            throw new IllegalArgumentException("Game not found with id: " + id);
        }

        // Apply a default image and remove existing enigmas if the game lacks an image
        if (game.getImage() == null || game.getImage().isBlank()) {
            game.setImage("https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");

            // Remove existing enigmas to avoid inconsistent states
            for (Phase phase : game.getPhases()) {
                if (phase.getEnigmas() != null) {
                    List<Long> enigmaIds = new ArrayList<>();
                    for (Enigma enigma : phase.getEnigmas()) {
                        enigmaIds.add(enigma.getId());
                    }
                    for (Long enigmaId : enigmaIds) {
                        enigmaService.delete(enigmaId);
                    }
                }
            }
        }

        // Prepare phases and apply defaults on missing data
        Set<Phase> phases = game.getPhases();
        for (Phase phase : phases) {

            if (phase.getDescription() == null || phase.getDescription().isBlank()) {
                phase.setDescription("No description available");
            }
            if (phase.getLiteralText() == null || phase.getLiteralText().isBlank()) {
                phase.setLiteralText("Phase " + phase.getPhaseName());
            }

            // Normalize enigmas
            for (Enigma e : phase.getEnigmas()) {
                if (e.getQuestion() == null || e.getQuestion().isBlank()) {
                    e.setQuestion("No statement defined yet");
                }
                if (e.getAnswerFormat() == null || e.getAnswerFormat().isBlank()) {
                    e.setAnswerFormat("No answer format defined");
                }
            }
        }

        // Sort phases and enigmas by ID for consistent UI ordering
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
     * The list of game types is reordered so that the current type
     * appears first in the selection menu.
     */
    @GetMapping("/editGame2/{id}")
    public String editGame(@PathVariable Long id, Model model, RedirectAttributes ra) {

        Game dbGame = gameService.findGameById(id);

        if (dbGame == null) {
            ra.addFlashAttribute("errorMessage", "❌ Game not found.");
            return "redirect:/";
        }

        // Prepare type list with the current type pre-selected
        for (GameType type : typeGameService.findAll()) {
            type.setSelected(type.getCode().equals(dbGame.getGameType()));
        }

        model.addAttribute("typesGame", typeGameService.findAll());
        model.addAttribute("game", dbGame);

        // Build structured rows for phases and their respective enigmas
        List<Phase> orderedPhases = new ArrayList<>(dbGame.getPhases());
        orderedPhases.sort(Comparator.comparing(Phase::getId));

        List<Map<String, Object>> phaseRows = new ArrayList<>();
        int i = 0;

        for (Phase phase : orderedPhases) {
            Map<String, Object> row = new HashMap<>();
            row.put("idx", i);

            List<Enigma> sortedEnigmas = new ArrayList<>(phase.getEnigmas());
            sortedEnigmas.sort(Comparator.comparing(Enigma::getId));

            List<Map<String, Object>> enigmaRows = new ArrayList<>();
            int j = 0;

            for (Enigma enigma : sortedEnigmas) {
                Map<String, Object> erow = new HashMap<>();
                erow.put("eidx", j);
                erow.put("enigma", enigma);
                enigmaRows.add(erow);
                j++;
            }

            row.put("phase", phase);
            row.put("enigmaRows", enigmaRows);
            phaseRows.add(row);

            i++;
        }

        model.addAttribute("phaseRows", phaseRows);
        return "editGame2";
    }

    /**
     * Updates a game and all its associated phases and enigmas.
     *
     * The method:
     *  1. Updates basic game fields  
     *  2. Iterates through dynamically sent phaseRows[X] entries  
     *  3. For each phase, updates nested enigmas based on parameter keys  
     *  4. Validates completeness of the game, each phase, and each enigma  
     *
     * Any missing or incomplete data triggers a warning message, while
     * complete updates generate a success confirmation.
     */
    @PostMapping("/edit/game/{id}")
    public String updateGame(
            @PathVariable Long id,
            @ModelAttribute("game") Game formGame,
            @RequestParam Map<String, String> params,
            RedirectAttributes ra) {

        try {
            Game dbGame = gameService.findGameById(id);
            if (dbGame == null) {
                ra.addFlashAttribute("errorMessage", "❌ Game not found.");
                return "redirect:/";
            }

            // ---------- UPDATE BASIC GAME DATA ----------
            dbGame.setName(formGame.getName());
            dbGame.setDescription(formGame.getDescription());
            dbGame.setGameType(formGame.getGameType());
            dbGame.setImage(formGame.getImage());
            dbGame.setVideo(formGame.getVideo());
            dbGame.setHasLeaderboard(formGame.isHasLeaderboard());
            dbGame.setManual(formGame.getManual());

            // ---------- UPDATE PHASES ----------
            List<Phase> updatedPhases = new ArrayList<>();
            int idx = 0;

            while (params.containsKey("phaseRows[" + idx + "].id")) {

                Long phaseId = Long.parseLong(params.get("phaseRows[" + idx + "].id"));
                Phase phase = phaseService.findPhaseById(phaseId);

                if (phase == null) {
                    phase = new Phase();
                    phase.setId(phaseId);
                }

                // Update phase fields
                phase.setPhaseName(params.get("phaseRows[" + idx + "].phaseName"));
                phase.setDescription(params.get("phaseRows[" + idx + "].description"));
                phase.setImage(params.get("phaseRows[" + idx + "].image"));
                phase.setVideo(params.get("phaseRows[" + idx + "].video"));
                phase.setLatitude(params.get("phaseRows[" + idx + "].latitude"));
                phase.setLongitude(params.get("phaseRows[" + idx + "].longitude"));
                phase.setMapUrl(params.get("phaseRows[" + idx + "].mapUrl"));
                phase.setGame(dbGame);

                // ---------- UPDATE ENIGMAS INSIDE THIS PHASE ----------
                List<Enigma> enigmas = new ArrayList<>();

                for (String key : params.keySet()) {
                    if (key.startsWith("enigmas[")) {
                        try {
                            Long enigmaId = Long.parseLong(key.substring(8, key.indexOf("]")));
                            Enigma enigma = enigmaService.findEnigmaById(enigmaId);

                            if (enigma == null) continue;

                            // Only process enigmas belonging to this phase
                            if (enigma.getPhase() != null &&
                                enigma.getPhase().getId().equals(phaseId)) {

                                // Safely update every editable field
                                enigma.setLiteralText(
                                        safe(params.get("enigmas[" + enigmaId + "].literalText")));
                                enigma.setQuestion(
                                        safe(params.get("enigmas[" + enigmaId + "].statement")));
                                enigma.setAnswer(
                                        safe(params.get("enigmas[" + enigmaId + "].answer")));
                                enigma.setHint1(
                                        safe(params.get("enigmas[" + enigmaId + "].hint1")));
                                enigma.setHint2(
                                        safe(params.get("enigmas[" + enigmaId + "].hint2")));

                                if (params.get("enigmas[" + enigmaId + "].image") == null ||
                                        params.get("enigmas[" + enigmaId + "].image").isBlank()) {
                                    enigma.setImage(
                                            "https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");
                                } else {
                                    enigma.setImage(params.get("enigmas[" + enigmaId + "].image"));
                                }

                                enigma.setAnswerFormat(
                                        safe(params.get("enigmas[" + enigmaId + "].answerFormat")));

                                enigma.setEnigmaVideo(
                                        safe(params.get("enigmas[" + enigmaId + "].video")));

                                enigma.setPhase(phase);
                                enigmas.add(enigma);
                            }

                        } catch (Exception ignored) {}
                    }
                }

                if (!enigmas.isEmpty()) {
                    phase.getEnigmas().clear();
                    phase.getEnigmas().addAll(enigmas);
                }

                updatedPhases.add(phase);
                idx++;
            }

            // Replace old phases with updated ones
            dbGame.getPhases().clear();
            for (Phase phase : updatedPhases) {
                phase.setGame(dbGame);
                dbGame.getPhases().add(phase);
            }

            // ---------- SAVE AND VALIDATE COMPLETENESS ----------
            gameService.saveGame(dbGame);

            boolean incomplete = false;

            if (isEmptyGame(dbGame)) {
                incomplete = true;
                ra.addFlashAttribute("successMessage",
                        "⚠️ The game is incomplete. Please review phases and enigmas.");
            }

            for (Phase phase : dbGame.getPhases()) {
                if (incomplete) break;

                if (isEmptyPhase(phase)) {
                    incomplete = true;
                    ra.addFlashAttribute("successMessage",
                            "⚠️ The phase '" + phase.getPhaseName() + "' is incomplete.");
                    break;
                }

                for (Enigma enigma : phase.getEnigmas()) {
                    if (incomplete) break;

                    if (isEmptyEnigma(enigma)) {
                        incomplete = true;
                        ra.addFlashAttribute("successMessage",
                                "⚠️ The enigma '" + enigma.getLiteralText()
                                + "' in phase '" + phase.getPhaseName() + "' is incomplete.");
                        break;
                    }
                }
            }

            if (!incomplete) {
                ra.addFlashAttribute("successMessage", "💾 Changes saved successfully.");
            }

            return "redirect:/editGame2/" + id;

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("errorMessage", "❌ Error while saving: " + e.getMessage());
            return "redirect:/editGame2/" + id;
        }
    }

    // -------------------------------------------------------------------------
    // Utility Validation Methods
    // -------------------------------------------------------------------------

    /**
     * Safely returns a non-null string (converts null → empty).
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
     * Checks if the game contains empty required fields.
     */
    public boolean isEmptyGame(Game game) {
        if (game == null) return true;

        return game.getName().isEmpty()
                || game.getGameType().isEmpty()
                || game.getDescription().isEmpty()
                || game.getImage().isEmpty()
                || game.getVideo().isEmpty();
    }

    /**
     * Checks if a phase contains empty required fields.
     */
    public boolean isEmptyPhase(Phase phase) {
        if (phase == null) return true;

        return phase.getPhaseName().isEmpty()
                || phase.getLiteralText().isEmpty()
                || phase.getDescription().isEmpty()
                || phase.getImage().isEmpty()
                || phase.getVideo().isEmpty()
                || phase.getLatitude().isEmpty()
                || phase.getLongitude().isEmpty()
                || phase.getMapUrl().isEmpty();
    }

    /**
     * Checks if an enigma contains empty required fields or missing numeric values.
     */
    public boolean isEmptyEnigma(Enigma enigma) {
        if (enigma == null) return true;

        boolean emptyStrings =
                enigma.getLiteralText().isEmpty()
                || enigma.getImage().isEmpty()
                || enigma.getLocation().isEmpty()
                || enigma.getIntroduction().isEmpty()
                || enigma.getIntroAvatarVideo().isEmpty()
                || enigma.getEnigmaVideo().isEmpty()
                || enigma.getQuestion().isEmpty()
                || enigma.getAnswerFormat().isEmpty()
                || enigma.getHint1().isEmpty()
                || enigma.getHint2().isEmpty()
                || enigma.getAnswer().isEmpty()
                || enigma.getExplanationSpot().isEmpty()
                || enigma.getExplanationSpotVideo().isEmpty()
                || enigma.getLocationResolutionPhoto().isEmpty()
                || enigma.getLatitude().isEmpty()
                || enigma.getLongitude().isEmpty()
                || enigma.getAdditionalInstructions().isEmpty();

        boolean emptyNumbers =
                enigma.getEnigmaNumber() == null
                || enigma.getPointsCorrect() == null
                || enigma.getPointsFail() == null
                || enigma.getPointsHint1() == null
                || enigma.getPointsHint2() == null
                || enigma.getMaxTime() == null;

        return emptyStrings || emptyNumbers;
    }

    // -------------------------------------------------------------------------
    // Global Exception Handler
    // -------------------------------------------------------------------------

    /**
     * Global exception handler for missing request parameters.
     * Produces user-friendly error messages instead of raw exceptions.
     */
    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public String handleMissingParams(MissingServletRequestParameterException ex, Model model) {
            model.addAttribute("message", "Missing required parameter: " + ex.getParameterName());
            return "error";
        }
    }

    /**
     * Displays the edit page for phases and enigmas (step 1 in the editing flow).
     * Similar logic to the viewGame() handler but adapted for the editing UI.
     */
    @GetMapping("/editGames1/{id}")
    public String editGame1(@PathVariable("id") Long id, Model model) {

        Game game = gameService.findGameById(id);
        if (game == null) {
            throw new IllegalArgumentException("Game not found with id: " + id);
        }

        // Apply default placeholder image and remove inconsistencies
        if (game.getImage() == null || game.getImage().isBlank()) {
            game.setImage("https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");

            for (Phase phase : game.getPhases()) {
                if (phase.getEnigmas() != null) {
                    List<Long> enigmaIds = new ArrayList<>();
                    for (Enigma enigma : phase.getEnigmas()) {
                        enigmaIds.add(enigma.getId());
                    }
                    for (Long enigmaId : enigmaIds) {
                        enigmaService.delete(enigmaId);
                    }
                }
            }
        }

        // Prepare phases and assign default texts
        Set<Phase> phases = game.getPhases();
        for (Phase phase : phases) {
            if (phase.getDescription() == null || phase.getDescription().isBlank()) {
                phase.setDescription("No description available");
            }
            if (phase.getLiteralText() == null || phase.getLiteralText().isBlank()) {
                phase.setLiteralText("Phase " + phase.getPhaseName());
            }

            for (Enigma e : phase.getEnigmas()) {
                if (e.getQuestion() == null || e.getQuestion().isBlank()) {
                    e.setQuestion("No statement defined yet");
                }
                if (e.getAnswerFormat() == null || e.getAnswerFormat().isBlank()) {
                    e.setAnswerFormat("No answer format defined");
                }
            }
        }

        // Order phases and enigmas by ID
        List<Phase> sortedPhases = new ArrayList<>(game.getPhases());
        sortedPhases.sort(Comparator.comparing(Phase::getId));

        for (Phase phase : sortedPhases) {
            List<Enigma> sortedEnigmas = new ArrayList<>(phase.getEnigmas());
            sortedEnigmas.sort(Comparator.comparing(Enigma::getId));
            phase.setEnigmas(new LinkedHashSet<>(sortedEnigmas));
        }

        model.addAttribute("phases", sortedPhases);
        model.addAttribute("game", game);
        return "editGame1";
    }
}
