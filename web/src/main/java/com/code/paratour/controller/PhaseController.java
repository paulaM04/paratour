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

    /**
     * Displays the first step of the new game creation process.
     * Loads all available game types into the model.
     */
    @GetMapping("/newGame_get1")
    public String newGame_get1(Model model) {
        model.addAttribute("typesGame", typeGameService.findAll());
        return "newGame_1";
    }

    /**
     * Step 2 of the game creation process.
     * Generates placeholder objects for the number of phases specified by the user.
     * Each placeholder is used to dynamically render input fields in the next form.
     */
    @GetMapping("/newGame_get2")
    public String newGame_get2(
            @RequestParam("numPhases") int numPhases,
            @RequestParam Map<String, String> params,
            Model model) {
        try {
            // Generate placeholder Phase objects (for UI rendering)
            List<Phase> phases = new ArrayList<>();
            for (int i = 0; i < numPhases; i++) {
                Phase p = new Phase();
                p.setIdFalse(i + 1); // Temporary index for form usage
                phases.add(p);
            }

            model.addAttribute("phases", phases);
            copyGameParamsToModel(params, model);
            return "newGame_2";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    /**
     * Step 1 submission: receives game data and redirects to the phase configuration view.
     * The parameters are forwarded via redirect attributes to preserve user input.
     */
    @PostMapping("/newGame_post1")
    public String newGame_post1(
            @RequestParam("gameName") String gameName,
            @RequestParam("gameDescription") String gameDescription,
            @RequestParam("gameType") String gameType,
            @RequestParam(value = "gameImage", required = false) String gameImage,
            @RequestParam(value = "gameVideo", required = false) String gameVideo,
            @RequestParam(value = "hasLeaderboard", required = false) String hasLeaderboard,
            @RequestParam(value = "manual", required = false) String manual,
            @RequestParam("numPhases") int numPhases,
            org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {

        // Normalize checkbox values (default to true if unchecked)
        hasLeaderboard = (hasLeaderboard == null) ? "true" : hasLeaderboard;
        manual = (manual == null) ? "true" : manual;

        // Add all collected parameters to the redirect query string
        ra.addAttribute("numPhases", numPhases);
        ra.addAttribute("gameName", gameName);
        ra.addAttribute("gameDescription", gameDescription);
        ra.addAttribute("gameType", gameType);
        ra.addAttribute("gameImage", gameImage);
        ra.addAttribute("gameVideo", gameVideo);
        ra.addAttribute("hasLeaderboard", hasLeaderboard);
        ra.addAttribute("manual", manual);

        // Redirect to the phase configuration step
        return "redirect:/newGame_get2";
    }

    /**
     * Generates the form used to configure phases and riddles (enigmas).
     * Similar to newGame_get2, but used for more complex step navigation.
     */
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

    /**
     * Final step of the game creation flow.
     * Saves the game, its phases, and all associated riddles (enigmas).
     * Each phase and riddle is dynamically reconstructed from request parameters.
     */
    @PostMapping("/newGame_lastPost")
    public String newGame_lastPost(
            @RequestParam("phaseName") List<String> phaseNames,
            @RequestParam("description") List<String> descriptions,
            @RequestParam("numRiddles") List<Integer> numRiddles,
            @RequestParam Map<String, String> params,
            Model model) {
        try {
            // Create a new Game entity
            Game game = new Game();

            // Validate and retrieve the selected game type
            String selectedType = params.get("gameType");
            GameType type = typeGameService.findByCode(selectedType);
            if (type == null) {
                throw new IllegalArgumentException("Invalid game type: " + selectedType);
            }

            // Set base game attributes
            game.setName(params.get("gameName"));
            game.setDescription(params.get("gameDescription"));
            game.setGameType(params.get("gameType"));
            game.setImage(params.get("gameImage"));
            game.setVideo(params.get("gameVideo"));
            game.setHasLeaderboard(Boolean.parseBoolean(params.getOrDefault("hasLeaderboard", "true")));
            game.setManual(Boolean.parseBoolean(params.getOrDefault("manual", "true")));
            game.setNumberOfRiddles(0);

            // Save the game first to assign a persistent ID
            gameService.saveGame(game);

            List<Phase> savedPhases = new ArrayList<>();

            // Iterate over phases submitted by the form
            for (int i = 0; i < phaseNames.size(); i++) {
                Phase phase = new Phase();
                phase.setPhaseName(phaseNames.get(i));
                phase.setDescription(descriptions.get(i));

                // Retrieve additional metadata for this phase from the request parameters
                phase.setLiteralText(params.getOrDefault("literalText[" + i + "]", ""));
                phase.setLatitude(params.getOrDefault("latitude[" + i + "]", "0.0"));
                phase.setLongitude(params.getOrDefault("longitude[" + i + "]", "0.0"));
                phase.setManual(Boolean.TRUE);
                phase.setGame(game);

                // Save phase before assigning enigmas
                Phase savedPhase = phaseService.save(phase);
                savedPhases.add(savedPhase);

                // Number of riddles (enigmas) in this phase
                int riddlesCount = numRiddles.get(i);

                // Iterate through each riddle and map request parameters dynamically
                for (int r = 0; r < riddlesCount; r++) {
                    String prefix = "phases[" + i + "].riddles[" + r + "].";

                    Enigma enigma = new Enigma();
                    enigma.setPhase(savedPhase);
                    enigma.setPhaseId(savedPhase.getId());
                    enigma.setEnigmaNumber(r + 1);

                    // Populate all possible fields for the enigma
                    enigma.setLiteralText(params.getOrDefault(prefix + "literalText", ""));
                    enigma.setStatement(params.getOrDefault(prefix + "enigma", ""));
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

                    // Save each enigma individually
                    enigmaService.save(enigma);
                }
            }

            // Compute total number of riddles and update the game
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

    /**
     * Utility method used to re-inject all game parameters
     * into the model between multiple request steps.
     */
    private void copyGameParamsToModel(Map<String, String> params, Model model) {
        model.addAttribute("gameName", params.getOrDefault("gameName", ""));
        model.addAttribute("gameDescription", params.getOrDefault("gameDescription", ""));
        model.addAttribute("gameType", params.getOrDefault("gameType", ""));
        model.addAttribute("gameImage", params.getOrDefault("gameImage", ""));
        model.addAttribute("gameVideo", params.getOrDefault("gameVideo", ""));
        model.addAttribute("hasLeaderboard", params.getOrDefault("hasLeaderboard", "true"));
        model.addAttribute("manual", params.getOrDefault("manual", "false"));
    }

    /**
     * Safely parses an integer value from a string.
     * Returns 0 if the value is null, empty, or invalid.
     */
    public Integer parseIntSafe(String value) {
        try {
            return (value == null || value.isEmpty()) ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
