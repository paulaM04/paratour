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
                    Set<Enigma> enigmas = phase.getEnigmas();
                    for (Enigma enigma : enigmas) {
                        phase.getEnigmas().remove(enigma);
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
            // First, delete all enigmas linked to each phase (avoid
            // ConcurrentModificationException)
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
        // Load and prepare associated phases and enigmas
        Set<Phase> phases = game.getPhases();
        for (Phase phase : phases) {
            if (phase.getDescription() == null || phase.getDescription().isBlank()) {
                phase.setDescription("No description available");
            }
            if (phase.getLiteralText() == null || phase.getLiteralText().isBlank()) {
                phase.setLiteralText("Phase " + phase.getPhaseName());
            }
            Set<Enigma> enigmas = phase.getEnigmas();
            for (Enigma e : enigmas) {
                if (e.getQuestion() == null || e.getQuestion().isBlank()) {
                    e.setQuestion("No hay enunciado definido todavía");
                }
                if (e.getAnswerFormat() == null || e.getAnswerFormat().isBlank()) {
                    e.setAnswerFormat("Sin formato definido");
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

        for (GameType type : typeGameService.findAll()) {
            type.setSelected(type.getCode().equals(dbGame.getGameType()));
        }
        model.addAttribute("typesGame", typeGameService.findAll());
        model.addAttribute("game", dbGame);

        // Construimos filas con índices
        List<Phase> ordered = new ArrayList<>(dbGame.getPhases());
        ordered.sort(Comparator.comparing(Phase::getId));

        List<Map<String, Object>> phaseRows = new ArrayList<>();
        int i = 0;
        for (Phase phase : ordered) {
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
        return "editGame";
    }

    @PostMapping("/edit/game/{id}")
    public String updateGame(
            @PathVariable Long id,
            @ModelAttribute("game") Game formGame,
            @RequestParam Map<String, String> params,
            RedirectAttributes ra) {

        try {
            Game dbGame = gameService.findGameById(id);
            if (dbGame == null) {
                ra.addFlashAttribute("errorMessage", "❌ Juego no encontrado.");
                return "redirect:/";
            }

            // ---------- DATOS BÁSICOS DEL JUEGO ----------
            dbGame.setName(formGame.getName());
            dbGame.setDescription(formGame.getDescription());
            dbGame.setGameType(formGame.getGameType());
            dbGame.setImage(formGame.getImage());
            dbGame.setVideo(formGame.getVideo());
            dbGame.setHasLeaderboard(formGame.isHasLeaderboard());
            dbGame.setManual(formGame.getManual());

            // ---------- FASES ----------
            List<Phase> updatedPhases = new ArrayList<>();
            int idx = 0;

            // Recorremos mientras existan filas phaseRows[X]
            while (params.containsKey("phaseRows[" + idx + "].id")) {

                Long phaseId = Long.parseLong(params.get("phaseRows[" + idx + "].id"));
                Phase phase = phaseService.findPhaseById(phaseId);
                if (phase == null) {
                    phase = new Phase();
                    phase.setId(phaseId);
                }

                phase.setPhaseName(params.get("phaseRows[" + idx + "].phaseName"));
                phase.setDescription(params.get("phaseRows[" + idx + "].description"));
                phase.setImage(params.get("phaseRows[" + idx + "].image"));
                phase.setVideo(params.get("phaseRows[" + idx + "].video"));
                phase.setLatitude(params.get("phaseRows[" + idx + "].latitude"));
                phase.setLongitude(params.get("phaseRows[" + idx + "].longitude"));
                phase.setMapUrl(params.get("phaseRows[" + idx + "].mapUrl"));
                phase.setGame(dbGame);

                // ---------- ENIGMAS ----------
                List<Enigma> enigmas = new ArrayList<>();
                for (String key : params.keySet()) {
                    if (key.startsWith("enigmas[")) {
                        try {
                            Long enigmaId = Long.parseLong(key.substring(8, key.indexOf("]")));
                            Enigma enigma = enigmaService.findEnigmaById(enigmaId);
                            if (enigma == null)
                                continue;

                            // Solo los que pertenecen a esta fase
                            if (enigma.getPhase() != null && enigma.getPhase().getId().equals(phaseId)) {

                                if (params.get("enigmas[" + enigmaId + "].literalText") == null ||
                                        params.get("enigmas[" + enigmaId + "].literalText").isBlank()) {
                                    enigma.setLiteralText("");
                                } else {
                                    enigma.setLiteralText(params.get("enigmas[" + enigmaId + "].literalText"));
                                }

                                if (params.get("enigmas[" + enigmaId + "].statement") == null ||
                                        params.get("enigmas[" + enigmaId + "].statement").isBlank()) {
                                    enigma.setQuestion("");
                                } else {
                                    enigma.setQuestion(params.get("enigmas[" + enigmaId + "].statement"));
                                }

                                if (params.get("enigmas[" + enigmaId + "].answer") == null ||
                                        params.get("enigmas[" + enigmaId + "].answer").isBlank()) {
                                    enigma.setAnswer("");
                                } else {
                                    enigma.setAnswer(params.get("enigmas[" + enigmaId + "].answer"));
                                }

                                if (params.get("enigmas[" + enigmaId + "].hint1") == null ||
                                        params.get("enigmas[" + enigmaId + "].hint1").isBlank()) {
                                    enigma.setHint1("");
                                } else {
                                    enigma.setHint1(params.get("enigmas[" + enigmaId + "].hint1"));
                                }

                                if (params.get("enigmas[" + enigmaId + "].hint2") == null ||
                                        params.get("enigmas[" + enigmaId + "].hint2").isBlank()) {
                                    enigma.setHint2("");
                                } else {
                                    enigma.setHint2(params.get("enigmas[" + enigmaId + "].hint2"));
                                }

                                if (params.get("enigmas[" + enigmaId + "].answerFormat") == null ||
                                        params.get("enigmas[" + enigmaId + "].answerFormat").isBlank()) {
                                    enigma.setAnswerFormat("");
                                } else {
                                    enigma.setHint1(params.get("enigmas[" + enigmaId + "].answerFormat"));
                                }

                                if (params.get("enigmas[" + enigmaId + "].image") == null ||
                                        params.get("enigmas[" + enigmaId + "].image").isBlank()) {
                                    enigma.setImage(
                                            "https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");
                                } else {
                                    enigma.setImage(params.get("enigmas[" + enigmaId + "].image"));
                                }

                                if (params.get("enigmas[" + enigmaId + "].video") == null ||
                                        params.get("enigmas[" + enigmaId + "].video").isBlank()) {
                                    enigma.setEnigmaVideo("");
                                } else {
                                    enigma.setEnigmaVideo(params.get("enigmas[" + enigmaId + "].video"));
                                }
                                enigma.setPhase(phase);
                                enigmas.add(enigma);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }

                if (!enigmas.isEmpty()) {
                    phase.getEnigmas().clear();
                    phase.getEnigmas().addAll(enigmas);
                }

                updatedPhases.add(phase);
                idx++;
            }

            // Reemplazamos las fases anteriores por las nuevas
            dbGame.getPhases().clear();
            for (Phase phase : updatedPhases) {
                phase.setGame(dbGame);
                dbGame.getPhases().add(phase);
            }

            // ---------- GUARDAR Y REDIRIGIR ----------
            gameService.saveGame(dbGame);
            boolean empty = false;

            if (empty = isEmptyGame(dbGame)) {
                empty = true;
                ra.addFlashAttribute("successMessage",
                        "⚠️ El juego está incompleto. Por favor, revisa las fases y enigmas.");
            }
            for (Phase phase : dbGame.getPhases()) {
                if (empty)
                    break;
                System.out.println("ITERANDO FASES");
                if (isEmptyPhase(phase)) {
                    empty = true;
                    ra.addFlashAttribute("successMessage", "⚠️ La fase '" + phase.getPhaseName()
                            + "' está incompleta. Por favor, revisa sus campos.");
                    break;
                }
                for (Enigma enigma : phase.getEnigmas()) {
                    if (empty)
                        break;
                    System.out.println("ITERANDO ENIGMAS");
                    if (isEmptyEnigma(enigma)) {
                        empty = true;
                        ra.addFlashAttribute("successMessage", "⚠️ El enigma '" + enigma.getLiteralText()
                                + "' de la fase '" + phase.getPhaseName() + "' está incompleto.");
                        break;
                    }
                }
            }
            if (!empty)
                ra.addFlashAttribute("successMessage", "💾 Cambios guardados correctamente.");

            return "redirect:/editGame/" + id;

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("errorMessage", "❌ Error al guardar: " + e.getMessage());
            return "redirect:/editGame/" + id;
        }
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

    public boolean isEmptyGame(Game game) {
        if (game == null)
            return true;

        boolean emptyStrings = game.getName().isEmpty()
                || game.getGameType().isEmpty()
                || game.getDescription().isEmpty()
                || game.getImage().isEmpty()
                || game.getVideo().isEmpty();

        // Si algo está vacío o nulo, retorna true
        return emptyStrings;
    }

    public boolean isEmptyPhase(Phase phase) {
        if (phase == null)
            return true;

        boolean emptyStrings = phase.getPhaseName().isEmpty()
                || phase.getLiteralText().isEmpty()
                || phase.getDescription().isEmpty()
                || phase.getImage().isEmpty()
                || phase.getVideo().isEmpty()
                || phase.getLatitude().isEmpty()
                || phase.getLongitude().isEmpty()
                || phase.getMapUrl().isEmpty();

        // Si algo está vacío o nulo, retorna true
        return emptyStrings;
    }

    public boolean isEmptyEnigma(Enigma enigma) {
        if (enigma == null)
            return true;

        boolean emptyStrings = enigma.getLiteralText().isEmpty()
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

        boolean emptyNumbers = (enigma.getEnigmaNumber() == null)
                || (enigma.getPointsCorrect() == null)
                || (enigma.getPointsFail() == null)
                || (enigma.getPointsHint1() == null)
                || (enigma.getPointsHint2() == null)
                || (enigma.getMaxTime() == null);
        // Si algo está vacío o nulo, retorna true
        return emptyStrings && emptyNumbers;
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
