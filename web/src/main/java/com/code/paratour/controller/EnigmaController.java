package com.code.paratour.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.code.paratour.model.Enigma;
import com.code.paratour.model.Game;
import com.code.paratour.model.Phase;
import com.code.paratour.service.EnigmaService;
import com.code.paratour.service.GameService;
import com.code.paratour.service.PhaseService;
import com.code.paratour.service.TypeGameService;

/**
 * Controller responsible for managing the creation, editing, and deletion
 * of enigmas (riddles) associated with a specific phase within a game.
 *
 * This controller is part of a multi-step game creation flow and dynamically
 * handles the structure of phases and their corresponding enigmas.
 */
@Controller
public class EnigmaController {

    @Autowired
    private GameService gameService;

    @Autowired
    private PhaseService phaseService;

    @Autowired
    private EnigmaService enigmaService;

    @Autowired
    private TypeGameService typeGameService;

    /**
     * Receives the list of phases created in the previous step and builds
     * a hierarchical structure ready to be rendered in the "newGame_3" view.
     *
     * The resulting view allows the user to input the enigmas belonging to
     * each phase.
     *
     * This method transforms parallel lists received from the form into a
     * strongly structured model:
     *      Phase → List<RiddlePlaceholder>
     *
     * It also reinjects global game parameters to preserve data consistency
     * throughout the multi-step process.
     */
    @PostMapping("/newGame_post3")
    public String newGame_post3(
            @RequestParam("phaseName") List<String> phaseNames,
            @RequestParam("description") List<String> descriptions,
            @RequestParam("numRiddles") List<Integer> numRiddles,
            @RequestParam("literalText") List<String> literalTexts,
            @RequestParam("latitude") List<String> latitudes,
            @RequestParam("longitude") List<String> longitudes,
            @RequestParam Map<String, String> params,
            Model model) {

        try {
            // Defensive validation: a phase cannot contain a negative number of riddles.
            if (numRiddles.stream().anyMatch(n -> n < 0)) {
                throw new IllegalArgumentException("Riddle count cannot be negative.");
            }

            // Final structure containing all phases and their riddle placeholders.
            List<Map<String, Object>> phasesForView = new ArrayList<>();

            // Iterate through all phases received from the form.
            for (int i = 0; i < phaseNames.size(); i++) {

                // Map representing a single phase for the view layer.
                Map<String, Object> phase = new java.util.HashMap<>();
                phase.put("index", i);                           // technical index (0-based)
                phase.put("phaseName", phaseNames.get(i));
                phase.put("description", descriptions.get(i));
                phase.put("numRiddles", numRiddles.get(i));
                phase.put("literalText", literalTexts.get(i));
                phase.put("latitude", latitudes.get(i));
                phase.put("longitude", longitudes.get(i));
                phase.put("display", i + 1);                     // user-friendly index (1-based)

                // List of riddle placeholders to display in the UI.
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

            // Make the structured list of phases available to the view.
            model.addAttribute("phases", phasesForView);

            // Preserve global game parameters throughout the multi-step process.
            copyGameParamsToModel(params, model);

            return "newGame_3";

        } catch (Exception e) {
            // Generic error view for unexpected exceptions.
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    /**
     * Reinserts main game parameters into the model to ensure that they are
     * preserved across all steps of the creation process.
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
     * Adds a new enigma (riddle) to an existing phase.
     * This method is triggered from the game editing interface.
     *
     * The enigma is validated, normalized, and assigned an incremental
     * enigma number based on the current size of the phase.
     */
    @PostMapping("/addEnigma/{phaseId}")
    public String addEnigma(
            @PathVariable Long phaseId,
            @ModelAttribute Enigma newEnigma,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            // Load and validate the phase.
            Phase phase = phaseService.findPhaseById(phaseId);
            if (phase == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Phase not found.");
                return "redirect:/error";
            }

            // Ensure that the phase is associated with a valid game.
            Game game = phase.getGame();
            if (game == null) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "❌ The enigma cannot be linked to a non-existing game.");
                return "redirect:/error";
            }

            // Functional validation: the enigma must have a minimum literal text value.
            if (newEnigma.getLiteralText() != null && !newEnigma.getLiteralText().isBlank()) {

                // Link the enigma to the phase and normalize empty fields.
                newEnigma.setPhase(phase);
                newEnigma.fillEmptyFields();

                // Automatically assign the enigma number within the phase (1-based).
                newEnigma.setEnigmaNumber(phase.getEnigmas().size() + 1);

                // Assign a default question if none was provided.
                if (newEnigma.getQuestion() == null || newEnigma.getQuestion().isBlank()) {
                    newEnigma.setQuestion("Pending question for " + newEnigma.getLiteralText());
                }

                // Set a default image if none was provided.
                if (newEnigma.getImage() == null || newEnigma.getImage().isBlank()) {
                    newEnigma.setImage(
                        "https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png"
                    );
                }

                // Normalize optional fields to avoid null values.
                if (newEnigma.getEnigmaVideo() == null) newEnigma.setEnigmaVideo("");
                if (newEnigma.getLatitude() == null) newEnigma.setLatitude("");
                if (newEnigma.getLongitude() == null) newEnigma.setLongitude("");

                // Persist changes in phase, enigma, and game.
                phase.addEnigma(newEnigma);
                enigmaService.save(newEnigma);
                phaseService.save(phase);
                gameService.saveGame(game);

                redirectAttributes.addFlashAttribute("successMessage",
                        "✅ New enigma added successfully.");

            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "❌ You must provide a name for the enigma.");
            }

            return "redirect:/editGames1/" + game.getId();

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message",
                    e.getMessage() != null ? e.getMessage() : "Unknown error while adding the enigma.");
            return "error";
        }
    }

    /**
     * Deletes an existing enigma from its phase.
     * The phase and game relationships are updated to maintain integrity.
     */
    @PostMapping("/deleteEnigma/{enigmaId}")
    public String deleteEnigma(@PathVariable Long enigmaId,
                               RedirectAttributes redirectAttributes) {

        System.out.println("DELETE ENIGMA ID: " + enigmaId);

        try {
            Enigma enigma = enigmaService.findEnigmaById(enigmaId);
            Phase phase = enigma.getPhase();
            Game game = phase.getGame();

            if (game == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Game not found.");
                return "redirect:/error";
            }

            // Remove the enigma from the phase and persist changes.
            phase.getEnigmas().remove(enigma);
            phaseService.save(phase);
            gameService.saveGame(game);

            redirectAttributes.addFlashAttribute("successMessage", "✅ Enigma deleted successfully.");

            return "redirect:/editGames1/" + game.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "error";
        }
    }
}
