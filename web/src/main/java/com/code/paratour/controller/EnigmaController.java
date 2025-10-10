package com.code.paratour.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    private void copyGameParamsToModel(Map<String, String> params, Model model) {
        model.addAttribute("gameName", params.getOrDefault("gameName", ""));
        model.addAttribute("gameDescription", params.getOrDefault("gameDescription", ""));
        model.addAttribute("gameType", params.getOrDefault("gameType", ""));
        model.addAttribute("gameImage", params.getOrDefault("gameImage", ""));
        model.addAttribute("gameVideo", params.getOrDefault("gameVideo", ""));
        model.addAttribute("hasLeaderboard", params.getOrDefault("hasLeaderboard", "true"));
        model.addAttribute("manual", params.getOrDefault("manual", "false"));
    }

}
