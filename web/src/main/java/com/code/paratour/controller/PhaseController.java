package com.code.paratour.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class PhaseController {

    @Autowired
    private GameService gameService;

    @Autowired
    private PhaseService phaseService;

    @Autowired
    private EnigmaService enigmaService;

    @Autowired
    private TypeGameService typeGameService;

    // Default type used when no type is explicitly selected during creation
    public static String typeGeneric = "FREE";

    // -------------------------------------------------------------------------
    // STEP 1 — DISPLAY GAME CREATION START PAGE
    // -------------------------------------------------------------------------

    /**
     * Step 1 of the new game creation workflow.
     * Displays a form that allows the user to choose the basic properties of a new game.
     * Loads the list of available GameTypes for selection.
     */
    @GetMapping("/newGame_get1")
    public String newGame_get1(Model model) {
        model.addAttribute("typesGame", typeGameService.findAll());
        return "newGame_1";
    }

    // -------------------------------------------------------------------------
    // STEP 2 — GENERATE PHASE PLACEHOLDERS
    // -------------------------------------------------------------------------

    /**
     * Step 2 of the game creation process.
     * Generates a temporary list of Phase objects used as placeholders
     * to render dynamic input fields for each phase in the front-end.
     *
     * All game parameters collected in Step 1 are forwarded again
     * to preserve input across requests.
     */
    @GetMapping("/newGame_get2")
    public String newGame_get2(
            @RequestParam("numPhases") int numPhases,
            @RequestParam Map<String, String> params,
            Model model) {

        try {
            List<Phase> phases = new ArrayList<>();

            // Create dummy phases only for rendering the form
            for (int i = 0; i < numPhases; i++) {
                Phase p = new Phase();
                p.setIdFalse(i + 1); // Temporary ID for UI usage
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

    // -------------------------------------------------------------------------
    // STEP 1 FORM SUBMISSION — REDIRECT TO STEP 2
    // -------------------------------------------------------------------------

    /**
     * Handles submission of Step 1 (basic game information).
     * Redirects to Step 2, while passing all collected parameters
     * through URL redirect attributes.
     *
     * If the user specifies 0 phases, creates the game immediately
     * and skips the phase creation process.
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
            RedirectAttributes ra,
            Model model) {

        // Default inverted checkbox logic (if unchecked → true)
        hasLeaderboard = (hasLeaderboard == null) ? "true" : hasLeaderboard;
        manual = (manual == null) ? "true" : manual;

        // Forward all parameters to Step 2
        ra.addAttribute("numPhases", numPhases);
        ra.addAttribute("gameName", gameName);
        ra.addAttribute("gameDescription", gameDescription);

        // If no game type selected, fallback to a generic one
        if (gameType == null || gameType.isBlank()) {
            gameType = typeGeneric;
        }

        ra.addAttribute("gameType", gameType);
        ra.addAttribute("gameImage", gameImage);
        ra.addAttribute("gameVideo", gameVideo);
        ra.addAttribute("hasLeaderboard", hasLeaderboard);
        ra.addAttribute("manual", manual);

        // If no phases are defined, create an empty game and finish
        if (numPhases < 1) {
            Game game = new Game();
            game.setName(gameName);
            game.setDescription(gameDescription);
            game.setGameType(gameType);
            game.setImage(gameImage);
            game.setVideo(gameVideo);
            game.setHasLeaderboard(Boolean.parseBoolean(hasLeaderboard));
            game.setManual(Boolean.parseBoolean(manual));
            game.setNumberOfRiddles(0);

            gameService.saveGame(game);
            model.addAttribute("games", gameService.findAllGames());

            return "home";
        }

        return "redirect:/newGame_get2";
    }

    // -------------------------------------------------------------------------
    // ALTERNATIVE PHASE CREATION VIEW
    // -------------------------------------------------------------------------

    /**
     * Generates the combined form used to configure phases and riddles (enigmas)
     * in a single step. Similar to newGame_get2, but used for mixed workflows.
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

    // -------------------------------------------------------------------------
    // FINAL STEP — SAVE GAME, PHASES AND RIDDLES
    // -------------------------------------------------------------------------

    /**
     * Final step of the game creation flow.
     * Creates the Game object, persists it, and then reconstructs
     * all phases and their riddles (enigmas) from dynamic form data.
     */
    @PostMapping("/newGame_lastPost")
    public String newGame_lastPost(
            @RequestParam(value = "phaseName", required = false) List<String> phaseNames,
            @RequestParam(value = "description", required = false) List<String> descriptions,
            @RequestParam(value = "numRiddles", required = false) List<Integer> numRiddles,
            @RequestParam Map<String, String> params,
            Model model) {

        try {
            // Create base Game object
            Game game = new Game();

            // Validate game type
            String selectedType = params.get("gameType");
            GameType type = typeGameService.findByCode(selectedType);
            if (type == null) {
                throw new IllegalArgumentException("Invalid game type: " + selectedType);
            }

            // Assign base attributes
            game.setName(params.get("gameName"));
            game.setDescription(params.get("gameDescription"));
            game.setGameType(selectedType);
            game.setImage(params.get("gameImage"));
            game.setVideo(params.get("gameVideo"));
            game.setHasLeaderboard(Boolean.parseBoolean(params.getOrDefault("hasLeaderboard", "true")));
            game.setManual(Boolean.parseBoolean(params.getOrDefault("manual", "true")));
            game.setNumberOfRiddles(0);

            // Save game first to avoid transient reference issues
            gameService.saveGame(game);

            // If no phases exist, end creation early
            if (phaseNames == null || phaseNames.isEmpty()) {
                model.addAttribute("games", gameService.findAllGames());
                return "home";
            }

            List<Phase> savedPhases = new ArrayList<>();

            // Build each phase and its riddles
            for (int i = 0; i < phaseNames.size(); i++) {

                Phase phase = new Phase();
                phase.setPhaseName(phaseNames.get(i));
                phase.setDescription(descriptions.get(i));
                phase.setLiteralText(params.getOrDefault("literalText[" + i + "]", ""));
                phase.setLatitude(params.getOrDefault("latitude[" + i + "]", "0.0"));
                phase.setLongitude(params.getOrDefault("longitude[" + i + "]", "0.0"));
                phase.setManual(Boolean.TRUE);
                phase.setGame(game);

                // Save the phase
                Phase savedPhase = phaseService.save(phase);
                savedPhases.add(savedPhase);

                int riddlesCount =
                        (numRiddles != null && numRiddles.size() > i)
                        ? numRiddles.get(i)
                        : 0;

                // Build each enigma for this phase
                for (int r = 0; r < riddlesCount; r++) {

                    String prefix = "phases[" + i + "].riddles[" + r + "].";

                    Enigma enigma = new Enigma();
                    enigma.setPhase(savedPhase);
                    enigma.setEnigmaNumber(r + 1);

                    // Fill all enigma fields from dynamic parameters
                    enigma.setLiteralText(params.getOrDefault(prefix + "literalText", ""));
                    enigma.setQuestion(params.getOrDefault(prefix + "enigma", ""));
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

                    // Parse numeric fields safely
                    enigma.setPointsCorrect(parseIntSafe(params.get(prefix + "pointsCorrect")));
                    enigma.setPointsFail(parseIntSafe(params.get(prefix + "pointsFail")));
                    enigma.setPointsHint1(parseIntSafe(params.get(prefix + "pointsHint1")));
                    enigma.setPointsHint2(parseIntSafe(params.get(prefix + "pointsHint2")));
                    enigma.setMaxTime(parseIntSafe(params.get(prefix + "maxTime")));

                    enigma.setManual(Boolean.parseBoolean(params.getOrDefault(prefix + "manual", "true")));

                    enigmaService.save(enigma);
                }
            }

            // Compute total riddles across all phases
            int totalRiddles =
                    (numRiddles != null)
                    ? numRiddles.stream().mapToInt(Integer::intValue).sum()
                    : 0;
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

    // -------------------------------------------------------------------------
    // HELPERS & UTILITIES
    // -------------------------------------------------------------------------

    /**
     * Preserves all game parameters between steps of the creation workflow.
     * Used to repopulate views when navigating between multi-step forms.
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
     * Safely parses integers while avoiding NumberFormatException.
     * Returns 0 for null, empty, or invalid numeric values.
     */
    public Integer parseIntSafe(String value) {
        try {
            return (value == null || value.isEmpty()) ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // -------------------------------------------------------------------------
    // DELETE PHASE
    // -------------------------------------------------------------------------

    /**
     * Deletes a phase and all associated enigmas.
     * Ensures that the parent game still exists before deletion and
     * redirects back to the game editing page.
     */
    @Transactional
    @PostMapping("/deletePhase/{phaseId}")
    public String deletePhase(
            @PathVariable Long phaseId,
            RedirectAttributes redirectAttributes) {

        Phase phaseToDelete = phaseService.findPhaseById(phaseId);
        Game game = (phaseToDelete != null) ? phaseToDelete.getGame() : null;

        if (game == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Game not found.");
            return "redirect:/games";
        }

        if (phaseToDelete == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Phase not found.");
            return "redirect:/editGames1/" + game.getId();
        }

        // Delete all enigmas before deleting the phase
        enigmaService.deleteAll(phaseToDelete.getEnigmas());
        phaseService.delete(phaseToDelete.getId());
        gameService.saveGame(game);

        redirectAttributes.addFlashAttribute("successMessage", "✅ Phase deleted successfully.");
        return "redirect:/editGames1/" + game.getId();
    }

    // -------------------------------------------------------------------------
    // ADD PHASE TO AN EXISTING GAME
    // -------------------------------------------------------------------------

    /**
     * Adds a new phase to an existing game.
     * Applies default values to missing fields and validates mandatory ones.
     * Redirects the user back to the game editing view.
     */
    @PostMapping("/addPhase/{gameId}")
    public String addPhase(
            @PathVariable Long gameId,
            @ModelAttribute Phase newPhase,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            Game game = gameService.findGameById(gameId);

            if (newPhase.getPhaseName() != null && !newPhase.getPhaseName().isBlank()) {

                newPhase.setGame(game);

                // Apply default values to missing or empty fields
                if (newPhase.getLiteralText() == null || newPhase.getLiteralText().isBlank()) {
                    newPhase.setLiteralText("Phase " + newPhase.getPhaseName());
                }
                if (newPhase.getLatitude() == null || newPhase.getLatitude().isBlank()) {
                    newPhase.setLatitude("");
                }
                if (newPhase.getLongitude() == null || newPhase.getLongitude().isBlank()) {
                    newPhase.setLongitude("");
                }
                if (newPhase.getImage() == null || newPhase.getImage().isBlank()) {
                    newPhase.setImage(
                        "https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");
                }
                if (newPhase.getVideo() == null || newPhase.getVideo().isBlank()) {
                    newPhase.setVideo("");
                }

                game.addPhase(newPhase);
                gameService.saveGame(game);

                redirectAttributes.addFlashAttribute("successMessage",
                        "✅ New phase added successfully.");

            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "❌ You must provide a name for the phase.");
            }

            // Sort phases for a consistent display order
            List<Phase> sortedPhases = new ArrayList<>(game.getPhases());
            sortedPhases.sort(Comparator.comparing(Phase::getId));

            model.addAttribute("phases", sortedPhases);
            model.addAttribute("game", game);

            return "redirect:/editGames1/" + gameId;

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message",
                    e.getMessage() != null ? e.getMessage() : "Unknown error");
            return "error";
        }
    }
}
