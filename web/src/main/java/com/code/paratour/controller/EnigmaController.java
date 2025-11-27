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
     * Recibe todos los datos de las fases desde el formulario anterior
     * y prepara dinámicamente la vista "newGame_3" donde el usuario introducirá
     * los enigmas de cada fase.
     *
     * Se reorganizan los datos de entrada (listas de nombres, descripciones…)
     * en una estructura jerárquica:
     *
     *      phases → riddles
     *
     * Cada fase contendrá una lista de "placeholders" para los enigmas.
     */
    @PostMapping("/newGame_post3")
    public String newGame_post3(
            @RequestParam("phaseName") List<String> phaseNames,
            @RequestParam("description") List<String> descriptions,
            @RequestParam("numRiddles") List<Integer> numRiddles,
            @RequestParam("literalText") List<String> literalTexts,
            @RequestParam("latitude") List<String> latitudes,
            @RequestParam("longitude") List<String> longitudes,
            @RequestParam Map<String, String> params,  // todos los parámetros del formulario
            Model model) {
        try {

            // Validación: ningún número de enigmas puede ser negativo
            if (numRiddles.stream().anyMatch(n -> n < 0)) {
                throw new IllegalArgumentException("El número de enigmas no puede ser negativo.");
            }

            // Lista que representará todas las fases que se enviarán a la plantilla
            List<Map<String, Object>> phasesForView = new ArrayList<>();

            // Recorremos cada índice de fase para construir su mapa de datos
            for (int i = 0; i < phaseNames.size(); i++) {

                // Mapa que contiene los datos básicos de la fase
                Map<String, Object> phase = new java.util.HashMap<>();
                phase.put("index", i);                      // índice técnico (0-based)
                phase.put("phaseName", phaseNames.get(i));
                phase.put("description", descriptions.get(i));
                phase.put("numRiddles", numRiddles.get(i));
                phase.put("literalText", literalTexts.get(i));
                phase.put("latitude", latitudes.get(i));
                phase.put("longitude", longitudes.get(i));

                // Índice human-friendly (1-based)
                phase.put("display", i + 1);

                // Lista de enigmas *vacíos* que se rellenarán en la plantilla
                List<Map<String, Object>> riddles = new ArrayList<>();

                // Se crean tantos "riddle placeholders" como se indicó
                for (int r = 0; r < numRiddles.get(i); r++) {
                    Map<String, Object> rid = new java.util.HashMap<>();
                    rid.put("phaseIndex", i);   // fase a la que pertenece
                    rid.put("idx", r);          // índice dentro de la fase
                    rid.put("display", r + 1);  // índice visible
                    riddles.add(rid);
                }

                // Insertamos los enigmas dentro de la fase
                phase.put("riddles", riddles);

                // Añadimos esta fase a la lista final
                phasesForView.add(phase);
            }

            // Añadimos al modelo la lista completa de fases construidas
            model.addAttribute("phases", phasesForView);

            // Reinyectamos los parámetros generales del juego
            copyGameParamsToModel(params, model);

            // Mostramos la siguiente vista de creación (donde se rellenan los enigmas)
            return "newGame_3";

        } catch (Exception e) {
            // Si ocurre un error inesperado, mostramos la vista de error
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    /**
     * Añade parámetros generales del juego al modelo,
     * para mantenerlos a lo largo del proceso multi-step.
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
     * POST que añade un nuevo enigma a una fase existente.
     * Se llama desde la pantalla de edición de juegos.
     */
    @PostMapping("/addEnigma/{phaseId}")
    public String addEnigma(
            @PathVariable Long phaseId,                // fase donde se insertará
            @ModelAttribute Enigma newEnigma,          // datos del formulario
            RedirectAttributes redirectAttributes,
            Model model) {
        try {

            // Cargamos la fase
            Phase phase = phaseService.findPhaseById(phaseId);
            if (phase == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Fase no encontrada.");
                return "redirect:/error";
            }

            // Obtenemos el juego al que pertenece
            Game game = phase.getGame();
            if (game == null) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "❌ El enigma no puede asociarse a un juego inexistente.");
                return "redirect:/error";
            }

            // Validación: debe tener al menos un nombre o texto literal
            if (newEnigma.getLiteralText() != null && !newEnigma.getLiteralText().isBlank()) {

                // Asociamos el enigma a la fase
                newEnigma.setPhase(phase);

                // Rellena campos nulos o vacíos con valores por defecto
                newEnigma.fillEmptyFields();

                // Asignación automática del número de enigma dentro de la fase
                newEnigma.setEnigmaNumber(phase.getEnigmas().size() + 1);

                // Valores por defecto si no se declararon
                if (newEnigma.getQuestion() == null || newEnigma.getQuestion().isBlank()) {
                    newEnigma.setQuestion("Pregunta pendiente para " + newEnigma.getLiteralText());
                }

                // Imagen por defecto si no se especifica ninguna
                if (newEnigma.getImage() == null || newEnigma.getImage().isBlank()) {
                    newEnigma.setImage(
                        "https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png"
                    );
                }

                // Valores vacíos aceptables pero normalizados
                if (newEnigma.getEnigmaVideo() == null) newEnigma.setEnigmaVideo("");
                if (newEnigma.getLatitude() == null) newEnigma.setLatitude("");
                if (newEnigma.getLongitude() == null) newEnigma.setLongitude("");

                // Guardamos el enigma en la BD y actualizamos relaciones
                phase.addEnigma(newEnigma);
                enigmaService.save(newEnigma);
                phaseService.save(phase);
                gameService.saveGame(game);

                redirectAttributes.addFlashAttribute("successMessage", "✅ Nuevo enigma añadido correctamente.");

            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Debes introducir un nombre para el enigma.");
            }

            // Volvemos a la edición del juego
            return "redirect:/editGames1/" + game.getId();

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message",
                    e.getMessage() != null ? e.getMessage() : "Error desconocido al añadir el enigma.");
            return "error";
        }
    }

    /**
     * Elimina un enigma de una fase.
     */
    @PostMapping("/deleteEnigma/{enigmaId}")
    public String deleteEnigma(@PathVariable Long enigmaId,
                               RedirectAttributes redirectAttributes) {

        System.out.println("DELETE ENIGMA ID: " + enigmaId);

        try {
            // Buscamos el enigma
            Enigma enigma = enigmaService.findEnigmaById(enigmaId);

            Phase phase = enigma.getPhase();
            Game game = phase.getGame();

            if (game == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Juego no encontrado.");
                return "redirect:/error";
            }

            // Eliminamos el enigma de la fase
            phase.getEnigmas().remove(enigma);
            phaseService.save(phase);
            gameService.saveGame(game);

            redirectAttributes.addFlashAttribute("successMessage", "✅ Enigma eliminado correctamente.");

            // Volvemos a la edición del juego
            return "redirect:/editGames1/" + game.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "error";
        }
    }
}
