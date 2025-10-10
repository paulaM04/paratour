package com.code.paratour.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.code.paratour.model.Enigma;
import com.code.paratour.model.Game;
import com.code.paratour.model.GameType;
import com.code.paratour.model.Phase;
import com.code.paratour.service.EnigmaService;
import com.code.paratour.service.GameService;
import com.code.paratour.service.PhaseService;
import com.code.paratour.service.TypeGameService;

@Controller
public class PhaseController {

    @Autowired
    private GameService gameService;

    @Autowired
    private PhaseService phaseService;

    @Autowired
    private EnigmaService enigmaService;

    @Autowired
    private TypeGameService typeGameService;

    @GetMapping("/setupNumPhases")
    public String setupNumPhases(Model model) {
        model.addAttribute("typesGame", typeGameService.findAll());
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
            copyGameParamsToModel(params, model);

            return "newPhaseAndEnigma";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/savePhases")
    public String savePhases(
            @RequestParam("phaseName") List<String> phaseNames,
            @RequestParam("description") List<String> descriptions,
            @RequestParam("numRiddles") List<Integer> numRiddles,
            @RequestParam Map<String, String> params,
            Model model) {
        try {
            Game game = new Game();
            String tipoSeleccionado = params.get("gameType");
            GameType tipo = typeGameService.findByName(tipoSeleccionado);
            if (tipo == null) {
                throw new IllegalArgumentException("Tipo de juego inválido: " + tipoSeleccionado);
            }
            game.setName(params.get("gameName"));
            game.setDescription(params.get("gameDescription"));
            game.setGameType(params.get("gameType"));
            game.setImage(params.get("gameImage"));
            game.setVideo(params.get("gameVideo"));
            game.setHasLeaderboard(Boolean.parseBoolean(params.getOrDefault("hasLeaderboard", "true")));
            game.setManual(Boolean.parseBoolean(params.getOrDefault("manual", "true")));
            game.setNumberOfRiddles(0);

            gameService.saveGame(game);

            List<Phase> savedPhases = new ArrayList<>();

            for (int i = 0; i < phaseNames.size(); i++) {
                Phase phase = new Phase();
                phase.setPhaseName(phaseNames.get(i));
                phase.setDescription(descriptions.get(i));

                phase.setLiteralText(params.getOrDefault("literalText[" + i + "]", ""));
                phase.setLatitude(params.getOrDefault("latitude[" + i + "]", "0.0"));
                phase.setLongitude(params.getOrDefault("longitude[" + i + "]", "0.0"));
                phase.setManual(Boolean.TRUE);
                phase.setGame(game);

                Phase savedPhase = phaseService.save(phase);
                savedPhases.add(savedPhase);

                int riddlesCount = numRiddles.get(i);

                for (int r = 0; r < riddlesCount; r++) {
                    String prefix = "phases[" + i + "].riddles[" + r + "].";

                    Enigma enigma = new Enigma();
                    enigma.setPhase(savedPhase);
                    enigma.setPhaseId(savedPhase.getId());
                    enigma.setEnigmaNumber(r + 1);
                    enigma.setLiteralText(params.getOrDefault(prefix + "literalText", ""));
                    enigma.setEnigma(params.getOrDefault(prefix + "enigma", ""));
                    enigma.setAnswer(params.getOrDefault(prefix + "answer", ""));
                    enigma.setAnswerFormat(params.getOrDefault(prefix + "answerFormat", ""));
                    enigma.setHint1(params.getOrDefault(prefix + "hint1", ""));
                    enigma.setHint2(params.getOrDefault(prefix + "hint2", ""));
                    enigma.setExplanationSpot(params.getOrDefault(prefix + "explanationSpot", ""));
                    enigma.setImage(params.getOrDefault(prefix + "image", ""));
                    enigma.setLocation(params.getOrDefault(prefix + "location", ""));
                    enigma.setIntroduction(params.getOrDefault(prefix + "introduction", ""));
                    enigma.setIntroAvatarVideo(params.getOrDefault(prefix + "introAvatarVideo", ""));
                    enigma.setEnigmaVideo(params.getOrDefault(prefix + "enigmaVideo", ""));
                    enigma.setExplanationSpotVideo(params.getOrDefault(prefix + "explanationSpotVideo", ""));
                    enigma.setLocationResolutionPhoto(params.getOrDefault(prefix + "locationResolutionPhoto", ""));
                    enigma.setLatitude(params.getOrDefault(prefix + "latitude", "0.0"));
                    enigma.setLongitude(params.getOrDefault(prefix + "longitude", "0.0"));
                    enigma.setAdditionalInstructions(params.getOrDefault(prefix + "additionalInstructions", ""));
                    enigma.setPointsCorrect(parseIntSafe(params.get(prefix + "pointsCorrect")));
                    enigma.setPointsFail(parseIntSafe(params.get(prefix + "pointsFail")));
                    enigma.setPointsHint1(parseIntSafe(params.get(prefix + "pointsHint1")));
                    enigma.setPointsHint2(parseIntSafe(params.get(prefix + "pointsHint2")));
                    enigma.setMaxTime(parseIntSafe(params.get(prefix + "maxTime")));
                    enigma.setManual(Boolean.parseBoolean(params.getOrDefault(prefix + "manual", "true")));
                    enigmaService.save(enigma);
                }
            }

            int totalRiddles = numRiddles.stream().mapToInt(Integer::intValue).sum();
            game.setNumberOfRiddles(totalRiddles);

            gameService.saveGame(game);
            model.addAttribute("games", gameService.findAllGames());
            return "home";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", e.getMessage());
            return "error";
        }
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

        
    public Integer parseIntSafe(String value) {
        try {
            return (value == null || value.isEmpty()) ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}