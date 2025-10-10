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

import com.code.paratour.model.Enigma;
import com.code.paratour.model.Game;
import com.code.paratour.model.GameType;
import com.code.paratour.model.Phase;
import com.code.paratour.service.EnigmaService;
import com.code.paratour.service.GameService;
import com.code.paratour.service.PhaseService;
import com.code.paratour.service.TypeGameService;

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

    @GetMapping("/")
    public String home(Model model) {

        try {
            for (Game game : gameService.findAllGames()) {
                if (game.getImage() == null || game.getImage().isBlank()) {
                    game.setImage(
                            "https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");
                }
            }

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



    @GetMapping("/deleteGame/{id}")
    public String deleteGame(@PathVariable Long id, Model model) {
        try {
            Game game = gameService.findGameById(id);

            if (game == null) {
                model.addAttribute("message", "El juego con ID " + id + " no existe.");
                return "error";
            }

            // 🔹 Primero eliminar los enigmas de cada fase
            for (Phase phase : game.getPhases()) {
                if (phase.getEnigmas() != null) {
                    for (Enigma enigma : phase.getEnigmas()) {
                        enigmaService.delete(enigma.getId());
                    }
                }
            }

            for (Phase phase : game.getPhases()) {
                phaseService.delete(phase.getId());
            }

            gameService.deleteGame(id);

            model.addAttribute("games", gameService.findAllGames());
            model.addAttribute("successMessage", "Juego eliminado correctamente ✅");

            return "home";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Error al eliminar el juego: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/games/{id}")
    public String viewGame(@PathVariable("id") Long id, Model model) {
        Game game = gameService.findGameById(id);
        if (game == null) {
            throw new IllegalArgumentException("Juego no encontrado con id: " + id);
        }
        if (game.getImage() == null || game.getImage().isBlank()) {
            game.setImage("https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");
        }
        if (game.getVideo() == null || game.getVideo().isBlank()) {
            game.setVideo("");
        }
        if (game.getDescription() == null || game.getDescription().isBlank()) {
            game.setDescription("Sin descripción por el momento");
        }
        List<Phase> phases = phaseService.findByGameId(id);
        for (Phase phase : phases) {
            if (phase.getDescription() == null || phase.getDescription().isBlank()) {
                phase.setDescription("Sin descripción por el momento");
            }
            if (phase.getLiteralText() == null || phase.getLiteralText().isBlank()) {
                phase.setLiteralText("Fase " + phase.getPhaseName());
            }
            List<Enigma> enigmas = phase.getEnigmas();
            for (Enigma e : enigmas) {
                if (e.getEnigma() == null || e.getEnigma().isBlank()) {
                    e.setEnigma("Sin enigma por el momento");
                }
                if (e.getAnswerFormat() == null || e.getAnswerFormat().isBlank()) {
                    e.setAnswerFormat("sin formato por el momento");
                }
            }
        }

        // model.addAttribute("typesGame", typeGameService.findAll());
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
            if (phase.getDescription() == null || phase.getDescription().isBlank()) {
                phase.setDescription("");
            }
            if (phase.getEnigmas() != null) {
                for (Enigma e : phase.getEnigmas()) {
                    e.setIdidTreak(j++);
                    enigmas.add(e);
                    if (e.getEnigma() == null || e.getEnigma().isBlank()) {
                        e.setEnigma("");
                    }
                    if (e.getAnswerFormat() == null || e.getAnswerFormat().isBlank()) {
                        e.setAnswerFormat("");
                    }
                }
            } else {
                phase.setEnigmas(new ArrayList<>()); // nunca null
            }
        }
        if (game.getVideo() == null || game.getVideo().isBlank()) {
            game.setVideo("");
        }
        if (game.getImage() == null || game.getImage().isBlank()) {
            game.setImage("https://lacaja.paratourmadrid.com/juegos/juego-fase0/img-prueba-juegos-horizontal.png");
        }
        List<GameType> tipos = typeGameService.findAll();
        List<GameType> tiposOrdenados = new ArrayList<>();

        // Identifica el tipo actual del juego
        String tipoActual = game.getGameType() == null ? "" : game.getGameType().trim().toLowerCase();

        // Recorre todos los tipos
        for (GameType tipo : tipos) {
            boolean isSelected = tipo.getCode().trim().equalsIgnoreCase(tipoActual) ||
                    tipo.getName().trim().equalsIgnoreCase(tipoActual);

            tipo.setIsSelected(isSelected);
            if (isSelected) {
                // Añade primero el tipo actual
                tiposOrdenados.add(0, tipo);
            } else {
                // Añade el resto detrás
                tiposOrdenados.add(tipo);
            }
        }

        // Envía al modelo la lista reordenada
        model.addAttribute("typesGame", tiposOrdenados);

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
        for (int i = 0; i < dbGame.getPhases().size(); i++) {
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

            // Actualizar enigmas por índice
            if (formPhase.getEnigmas() != null) {
                for (int j = 0; j < dbPhase.getEnigmas().size(); j++) {

                    Enigma formEnigma = formPhase.getEnigmas().get(j);
                    System.out.println("ITERACION " + j);
                    Enigma dbEnigma = dbPhase.getEnigmas().get(j); // enigma original con id real

                    System.out.println("ENIGMA CON ID original " + formEnigma.getId());
                    System.out.println("ENIGMA CON ID FORM " + dbEnigma.getId());

                    dbEnigma.setEnigma(safe(formEnigma.getEnigma()));
                    dbEnigma.setAnswer(safe(formEnigma.getAnswer()));
                    dbEnigma.setHint1(safe(formEnigma.getHint1()));
                    dbEnigma.setHint2(safe(formEnigma.getHint2()));
                    dbEnigma.setAnswerFormat(safe(formEnigma.getAnswerFormat()));
                    dbEnigma.setPointsCorrect(safeInt(formEnigma.getPointsCorrect()));
                    dbEnigma.setPointsFail(safeInt(formEnigma.getPointsFail()));
                    dbEnigma.setPointsHint1(safeInt(formEnigma.getPointsHint1()));
                    dbEnigma.setPointsHint2(safeInt(formEnigma.getPointsHint2()));
                    dbEnigma.setImage(safe(formEnigma.getImage()));
                    dbEnigma.setLocation(safe(formEnigma.getLocation()));
                    dbEnigma.setIntroduction(safe(formEnigma.getIntroduction()));
                    dbEnigma.setIntroAvatarVideo(safe(formEnigma.getIntroAvatarVideo()));
                    dbEnigma.setEnigmaVideo(safe(formEnigma.getEnigmaVideo()));
                    dbEnigma.setExplanationSpot(safe(formEnigma.getExplanationSpot()));
                    dbEnigma.setExplanationSpotVideo(safe(formEnigma.getExplanationSpotVideo()));
                    dbEnigma.setLocationResolutionPhoto(safe(formEnigma.getLocationResolutionPhoto()));
                    dbEnigma.setMaxTime(safeInt(formEnigma.getMaxTime()));
                    dbEnigma.setLatitude(safe(formEnigma.getLatitude()));
                    dbEnigma.setLongitude(safe(formEnigma.getLongitude()));
                    dbEnigma.setAdditionalInstructions(safe(formEnigma.getAdditionalInstructions()));
                    dbEnigma.setManual(safeBool(formEnigma.getManual()));

                    dbEnigma.setPhase(dbPhase);
                }

            }
        }
        // Guardar con cascada
        gameService.saveGame(dbGame);
        return "redirect:/editGame/" + id + "?success=1";
    }

    public String safe(String value) {
        return (value == null) ? "" : value;
    }

    public Integer safeInt(Integer value) {
        return (value == null) ? 0 : value;
    }

    // Para decimales (Double)
    public Boolean safeBool(Boolean value) {
        return (value == null) ? true : value;
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
