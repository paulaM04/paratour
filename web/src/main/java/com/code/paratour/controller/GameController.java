package com.code.paratour.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.code.paratour.service.GameTypeService;
import com.code.paratour.service.PhaseService;

import jakarta.transaction.Transactional;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private PhaseService phaseService;

    @Autowired
    private GameTypeService tiposJuegoService;

    @Autowired
    private EnigmaService enigmaService;

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

    @GetMapping("/newGame")
    public String newGameForm(Model model) {
        // Solo si no vienen de un redirect/flash
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

    @Autowired
    private GameTypeService gameTypeService;

    @GetMapping("/setupNumPhases")
    public String setupNumPhases(Model model) {
        List<GameType> gameTypes = gameTypeService.findAll();
        model.addAttribute("gameTypes", gameTypes);
        return "newGameNumberPhase";
    }

    @GetMapping("/createPhasesForm")
    public String createPhasesFormIni(
            @RequestParam("numPhases") int numPhases,
            @RequestParam Map<String, String> params,
            Model model) {
        try {
            // Genera placeholders de fases
            List<Phase> phases = new ArrayList<>();
            for (int i = 0; i < numPhases; i++) {
                Phase p = new Phase();
                p.setIdFalse(i + 1);
                phases.add(p);
            }
            model.addAttribute("phases", phases);

            // Reinyecta TODOS los datos de juego para que el siguiente form los envíe como
            // hidden:
            copyGameParamsToModel(params, model);

            return "newPhaseAndEnigma";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    // helper
    private Integer parseIntSafe(String val) {
        try {
            return (val != null && !val.isEmpty()) ? Integer.parseInt(val) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @PostMapping("/setupPhases")
    public String setupPhasesFromGame(
            @RequestParam("gameName") String gameName,
            @RequestParam("gameDescription") String gameDescription,
            @RequestParam("gameType") String gameType,
            @RequestParam(value = "gameImage", required = false) String gameImage,
            @RequestParam(value = "gameVideo", required = false) String gameVideo,
            @RequestParam(value = "hasLeaderboard", required = false) String hasLeaderboard,
            @RequestParam(value = "manual", required = false) String manual,
            Model model) {

        // defaults por si vienen nulos (checkboxes)
        model.addAttribute("gameName", gameName);
        model.addAttribute("gameDescription", gameDescription);
        model.addAttribute("gameType", gameType);
        model.addAttribute("gameImage", gameImage);
        model.addAttribute("gameVideo", gameVideo);
        model.addAttribute("hasLeaderboard", (hasLeaderboard == null) ? "true" : hasLeaderboard);
        model.addAttribute("manual", (manual == null) ? "true" : manual);

        return "newGameNumberPhase";
    }

    // === 2) nº de fases -> formulario de fases (POST) ===
    @PostMapping("/createPhasesForm")
    public String createPhasesForm(
            @RequestParam("numPhases") int numPhases,
            @RequestParam Map<String, String> params,
            Model model) {
        try {
            List<Phase> phases = new ArrayList<>();
            for (int i = 0; i < numPhases; i++) {
                Phase p = new Phase();
                p.setIdFalse(i + 1);
                phases.add(p);
            }
            model.addAttribute("phases", phases);

            // Volcamos de nuevo TODOS los campos del juego al modelo
            copyGameParamsToModel(params, model);

            return "newPhaseAndEnigma";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/prepareEnigmas")
    public String prepareEnigmas(
            @RequestParam("phaseName") List<String> phaseNames,
            @RequestParam("description") List<String> descriptions,
            @RequestParam("numRiddles") List<Integer> numRiddles,
            @RequestParam("literalText") List<String> literalTexts,
            @RequestParam("latitude") List<String> latitudes,
            @RequestParam("longitude") List<String> longitudes,
            @RequestParam Map<String, String> params,
            Model model) {
        try {
            List<Map<String, Object>> phasesForView = new ArrayList<>();
            for (int i = 0; i < phaseNames.size(); i++) {
                Map<String, Object> phase = new java.util.HashMap<>();
                phase.put("index", i);
                phase.put("phaseName", phaseNames.get(i));
                phase.put("description", descriptions.get(i));
                phase.put("numRiddles", numRiddles.get(i));
                phase.put("literalText", literalTexts.get(i));
                phase.put("latitude", latitudes.get(i));
                phase.put("longitude", longitudes.get(i));

                phase.put("display", i + 1);

                List<Map<String, Object>> riddles = new ArrayList<>();
                for (int r = 0; r < numRiddles.get(i); r++) {
                    Map<String, Object> rid = new java.util.HashMap<>();
                    rid.put("phaseIndex", i);
                    rid.put("idx", r);
                    rid.put("display", r + 1);
                    riddles.add(rid);
                }
                phase.put("riddles", riddles);
                phasesForView.add(phase);
            }
            model.addAttribute("phases", phasesForView);

            // Reinyectar los datos del juego
            copyGameParamsToModel(params, model);

            return "newEnigma";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/games/{id}")
    public String viewGame(@PathVariable("id") Long id, Model model) {
        Game game = gameService.findGameById(id);
        if (game == null) {
            throw new IllegalArgumentException("Juego no encontrado con id: " + id);
        }

        List<Phase> phases = phaseService.findByGameId(id);
        phases.forEach(phase -> {
            phase.setEnigmas(enigmaService.findByPhaseName(phase.getId()));
        });
        model.addAttribute("game", game);
        model.addAttribute("phases", phases);
        return "gameView";
    }

    @GetMapping("/editGame/{id}")
    public String editGameForm(@PathVariable Long id, Model model) {
        Game game = gameService.findGameById(id);
        List<Enigma> enigmas = new ArrayList<Enigma>();

        int i = 0;
        for (Phase phase : game.getPhases()) {
            phase.setIdFalse(i++);
            int j = 0;
            if (phase.getEnigmas() != null) {
                for (Enigma e : phase.getEnigmas()) {
                    e.setIdFalse(j++);
                    enigmas.add(e);
                }
            } else {
                phase.setEnigmas(new ArrayList<>()); // nunca null
            }
        }
        model.addAttribute("enigmas", enigmas);
        model.addAttribute("phases", game.getPhases());
        model.addAttribute("game", game);
        return "editGame";
    }

    @PostMapping("/edit/game/{id}")
    public String updateGame(@PathVariable Long id, @ModelAttribute Game formGame) {
        Game dbGame = gameService.findGameById(id);

        // Actualizamos valores principales del juego
        dbGame.setName(formGame.getName());
        dbGame.setGameType(formGame.getGameType());
        dbGame.setDescription(formGame.getDescription());
        dbGame.setImage(formGame.getImage());
        dbGame.setVideo(formGame.getVideo());
        dbGame.setNumberOfRiddles(formGame.getNumberOfRiddles());
        dbGame.setHasLeaderboard(formGame.isHasLeaderboard());
        dbGame.setManual(formGame.getManual());

        // Actualizar fases por índice
        for (int i = 0; i < formGame.getPhases().size(); i++) {
            Phase formPhase = formGame.getPhases().get(i);
            Phase dbPhase = dbGame.getPhases().get(i); // fase original con id real

            dbPhase.setPhaseName(formPhase.getPhaseName());
            dbPhase.setDescription(formPhase.getDescription());
            dbPhase.setLatitude(formPhase.getLatitude());
            dbPhase.setLongitude(formPhase.getLongitude());
            dbPhase.setMapUrl(formPhase.getMapUrl());
            dbPhase.setGame(dbGame);

            if (dbPhase.getLiteralText() == null || dbPhase.getLiteralText().isBlank()) {
                dbPhase.setLiteralText("Fase " + dbPhase.getPhaseName());
            }

            System.out.println("PHASE CON ID original " + dbPhase.getPhaseName());
            System.out.println("PHASE CON ID FORM " + formPhase.getPhaseName());


            // Actualizar enigmas por índice
            if (formPhase.getEnigmas() != null) {
                for (int j = 0; j < formPhase.getEnigmas().size(); j++) {
                    Enigma formEnigma = formPhase.getEnigmas().get(j);
                    Enigma dbEnigma= dbPhase.getEnigmas().get(j); // enigma original con id real
            System.out.println("ENIGMA CON ID original " + formEnigma.getId());
            System.out.println("ENIGMA CON ID FORM " + dbEnigma.getId());

                    dbEnigma.setEnigma(formEnigma.getEnigma());
                    dbEnigma.setAnswer(formEnigma.getAnswer());
                    dbEnigma.setHint1(formEnigma.getHint1());
                    dbEnigma.setHint2(formEnigma.getHint2());
                    dbEnigma.setAnswerFormat(formEnigma.getAnswerFormat());
                    dbEnigma.setPointsCorrect(formEnigma.getPointsCorrect());
                    dbEnigma.setPointsFail(formEnigma.getPointsFail());
                    dbEnigma.setPointsHint1(formEnigma.getPointsHint1());
                    dbEnigma.setPointsHint2(formEnigma.getPointsHint2());
                    dbEnigma.setImage(formEnigma.getImage());
                    dbEnigma.setLocation(formEnigma.getLocation());
                    dbEnigma.setIntroduction(formEnigma.getIntroduction());
                    dbEnigma.setIntroAvatarVideo(formEnigma.getIntroAvatarVideo());
                    dbEnigma.setEnigmaVideo(formEnigma.getEnigmaVideo());
                    dbEnigma.setExplanationSpot(formEnigma.getExplanationSpot());
                    dbEnigma.setExplanationSpotVideo(formEnigma.getExplanationSpotVideo());
                    dbEnigma.setLocationResolutionPhoto(formEnigma.getLocationResolutionPhoto());
                    dbEnigma.setMaxTime(formEnigma.getMaxTime());
                    dbEnigma.setLatitude(formEnigma.getLatitude());
                    dbEnigma.setLongitude(formEnigma.getLongitude());
                    dbEnigma.setAdditionalInstructions(formEnigma.getAdditionalInstructions());
                    dbEnigma.setManual(formEnigma.getManual());

                    dbEnigma.setPhase(dbPhase);
                }

            }
        }
        // Guardar con cascada
        gameService.saveGame(dbGame);
        return "redirect:/editGame/" + id + "?success=1";
    }

    private void copyGameParamsToModel(Map<String, String> params, Model model) {
        model.addAttribute("gameName", params.getOrDefault("gameName", ""));
        model.addAttribute("gameDescription", params.getOrDefault("gameDescription", ""));
        model.addAttribute("gameType", params.getOrDefault("gameType", ""));
        model.addAttribute("gameImage", params.getOrDefault("gameImage", ""));
        model.addAttribute("gameVideo", params.getOrDefault("gameVideo", ""));
        model.addAttribute("hasLeaderboard", params.getOrDefault("hasLeaderboard", "true"));
        model.addAttribute("manual", params.getOrDefault("manual", "false"));
    }

    private String defaultIfBlank(String s, String d) {
        return (s == null || s.isBlank()) ? d : s;
    }

    private boolean parseBoolDefault(String s, boolean def) {
        return (s == null || s.isBlank()) ? def : Boolean.parseBoolean(s);
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public String handleMissingParams(MissingServletRequestParameterException ex, Model model) {
            model.addAttribute("message", "Faltan parámetros obligatorios: " + ex.getParameterName());
            return "error";
        }
    }

}
