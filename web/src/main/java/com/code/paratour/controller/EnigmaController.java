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
     * Handles the POST request that receives all phase data and
     * dynamically prepares a new view ("newGame_3") to input riddles
     * (enigmas) for each phase.
     *
     * The data for each phase (name, description, number of riddles, coordinates,
     * etc.)
     * is received from the previous form and is restructured into a
     * hierarchical format (phases → riddles) for rendering in the next template.
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
            if (numRiddles.stream().anyMatch(n -> n < 0)) {
                throw new IllegalArgumentException("El número de enigmas no puede ser negativo.");

            }
            // This list will hold all phase objects (each phase contains a list of riddles)
            List<Map<String, Object>> phasesForView = new ArrayList<>();

            // Iterate through each phase index to build its data map
            for (int i = 0; i < phaseNames.size(); i++) {
                Map<String, Object> phase = new java.util.HashMap<>();
                phase.put("index", i); // numerical index used in forms
                phase.put("phaseName", phaseNames.get(i));
                phase.put("description", descriptions.get(i));
                phase.put("numRiddles", numRiddles.get(i));
                phase.put("literalText", literalTexts.get(i));
                phase.put("latitude", latitudes.get(i));
                phase.put("longitude", longitudes.get(i));

                // Display index used for user-friendly numbering (1-based)
                phase.put("display", i + 1);

                // Create the riddle list (riddles = enigmas)
                List<Map<String, Object>> riddles = new ArrayList<>();

                // Build placeholder objects for each riddle in the current phase
                for (int r = 0; r < numRiddles.get(i); r++) {
                    Map<String, Object> rid = new java.util.HashMap<>();
                    rid.put("phaseIndex", i); // index of the parent phase
                    rid.put("idx", r); // index of the riddle within the phase
                    rid.put("display", r + 1); // display index (1-based)
                    riddles.add(rid);
                }

                // Add riddles list to the current phase
                phase.put("riddles", riddles);

                // Add the completed phase map to the list for the view
                phasesForView.add(phase);
            }

            // Add all constructed phases to the model for rendering in the next template
            model.addAttribute("phases", phasesForView);

            // Re-inject general game data into the model
            copyGameParamsToModel(params, model);

            // Return the next view where riddles will be configured
            return "newGame_3";

        } catch (Exception e) {
            // In case of any unexpected error, show the error view with a message
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    /**
     * Utility method that re-adds the general game parameters (metadata)
     * to the model. These values persist through the multi-step game creation flow.
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

    @PostMapping("/addEnigma/{phaseId}")
    public String addEnigma(
            @PathVariable Long phaseId,
            @ModelAttribute Enigma newEnigma,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {

            Phase phase = phaseService.findPhaseById(phaseId);
            if (phase == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Fase no encontrada.");
                return "redirect:/error";
            }

            Game game = phase.getGame();
            if (game == null) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "❌ El enigma no puede asociarse a un juego inexistente.");
                return "redirect:/error";
            }

            // Validación: el enigma debe tener al menos un nombre o texto literal
            if (newEnigma.getLiteralText() != null && !newEnigma.getLiteralText().isBlank()) {

                // Asociar fase
                newEnigma.setPhase(phase);

                // Rellenar campos vacíos
                newEnigma.fillEmptyFields();

                // Número de enigma automático
                newEnigma.setEnigmaNumber(phase.getEnigmas().size() + 1);

                // Valores por defecto si están vacíos
                if (newEnigma.getQuestion() == null || newEnigma.getQuestion().isBlank()) {
                    newEnigma.setQuestion("Pregunta pendiente para " + newEnigma.getLiteralText());
                }
                if (newEnigma.getImage() == null || newEnigma.getImage().isBlank()) {
                    newEnigma.setImage(
                            "https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");
                }
                if (newEnigma.getEnigmaVideo() == null || newEnigma.getEnigmaVideo().isBlank()) {
                    newEnigma.setEnigmaVideo("");
                }
                if (newEnigma.getLatitude() == null || newEnigma.getLatitude().isBlank()) {
                    newEnigma.setLatitude("");
                }
                if (newEnigma.getLongitude() == null || newEnigma.getLongitude().isBlank()) {
                    newEnigma.setLongitude("");
                }

                // Guardar relación
                phase.addEnigma(newEnigma);
                enigmaService.save(newEnigma);
                phaseService.save(phase);
                gameService.saveGame(game);

                redirectAttributes.addFlashAttribute("successMessage", "✅ Nuevo enigma añadido correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Debes introducir un nombre para el enigma.");
            }

            // List<Phase> sortedPhases = new ArrayList<>(game.getPhases());
            // sortedPhases.sort(Comparator.comparing(Phase::getId));
            // model.addAttribute("phases", sortedPhases);
            //model.addAttribute("game", game);
            return "redirect:/editGame/" + game.getId();

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message",
                    e.getMessage() != null ? e.getMessage() : "Error desconocido al añadir el enigma.");
            return "error";
        }
    }

    @PostMapping("/deleteEnigma/{enigmaId}")
    public String deleteEnigma(@PathVariable Long enigmaId,
            RedirectAttributes redirectAttributes) {

        Enigma enigma = enigmaService.findEnigmaById(enigmaId);
        Phase phase = enigma.getPhase();
        Game game = phase.getGame();
        if (game == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Juego no encontrado.");
            return "redirect:/games";
        }

        phase.getEnigmas().remove(enigma);
        phaseService.save(phase);
        gameService.saveGame(game);
        redirectAttributes.addFlashAttribute("successMessage", "✅ Enigma eliminado correctamente.");
        return "redirect:/editGame/" + game.getId();
    }

}
