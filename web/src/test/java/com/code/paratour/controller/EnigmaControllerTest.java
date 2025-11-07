package com.code.paratour.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.code.paratour.service.EnigmaService;
import com.code.paratour.service.GameService;
import com.code.paratour.service.PhaseService;
import com.code.paratour.service.TypeGameService;

public class EnigmaControllerTest {

    @InjectMocks
    private EnigmaController enigmaController;

    @Mock
    private GameService gameService;

    @Mock
    private PhaseService phaseService;

    @Mock
    private EnigmaService enigmaService;

    @Mock
    private TypeGameService typeGameService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------------------------------------------------------
    // newGame_post3 - SUCCESS
    // ----------------------------------------------------------
    @Test
    void testNewGame_post3_Success() {
        List<String> phaseNames = List.of("Phase A");
        List<String> descriptions = List.of("Description A");
        List<Integer> numRiddles = List.of(2);
        List<String> literalTexts = List.of("Literal A");
        List<String> latitudes = List.of("40.1");
        List<String> longitudes = List.of("-3.7");

        Map<String, String> params = Map.of(
                "gameName", "Game Test",
                "gameDescription", "Desc Test",
                "gameType", "TYPE1"
        );

        String result = enigmaController.newGame_post3(
                phaseNames, descriptions, numRiddles, literalTexts,
                latitudes, longitudes, params, model
        );

        // Verifica que se añadió la lista "phases" al modelo
        verify(model).addAttribute(org.mockito.ArgumentMatchers.eq("phases"), any(List.class));
        // Verifica que se añadieron los parámetros del juego
        verify(model).addAttribute("gameName", "Game Test");
        assertEquals("newGame_3", result);
    }

    // ----------------------------------------------------------
    // newGame_post3 - ERROR (Exception)
    // ----------------------------------------------------------
    @Test
    void testNewGame_post3_Exception() {
        // Creamos listas vacías para forzar una excepción (index error)
        List<String> phaseNames = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();
        List<Integer> numRiddles = new ArrayList<>();
        List<String> literalTexts = new ArrayList<>();
        List<String> latitudes = new ArrayList<>();
        List<String> longitudes = new ArrayList<>();

        String result = enigmaController.newGame_post3(
                phaseNames, descriptions, numRiddles, literalTexts,
                latitudes, longitudes, Map.of(), model
        );

        verify(model).addAttribute(org.mockito.ArgumentMatchers.eq("phases"), any(List.class));
        // Si no lanza excepción, sigue siendo "newGame_3"
        assertEquals("newGame_3", result);
    }

    // ----------------------------------------------------------
    // addEnigma - SUCCESS
    // ----------------------------------------------------------
    // @Test
    // void testAddEnigma_Success() {
    //     Game game = new Game();
    //     game.setId(1L);
    //     Phase phase = new Phase();
    //     phase.setGame(game);
    //     phase.setId(10L);
    //     phase.setEnigmas(new HashSet<>());

    //     when(phaseService.findPhaseById(10L)).thenReturn(phase);

    //     Enigma enigma = new Enigma();
    //     enigma.setStatement("Enigma A");

    //     String result = enigmaController.addEnigma(10L, enigma, redirectAttributes);

    //     verify(enigmaService).save(any(Enigma.class));
    //     verify(phaseService).save(any(Phase.class));
    //     verify(gameService).saveGame(any(Game.class));
    //     verify(redirectAttributes).addFlashAttribute("successMessage", "✅ Nueva fase añadida correctamente.");
    //     assertEquals("redirect:/editGame/1", result);
    // }

    // ----------------------------------------------------------
    // addEnigma - PHASE NOT FOUND
    // ----------------------------------------------------------
    // @Test
    // void testAddEnigma_PhaseNotFound() {
    //     when(phaseService.findPhaseById(99L)).thenReturn(null);

    //     Enigma enigma = new Enigma();
    //     enigma.setStatement("E1");

    //     String result = enigmaController.addEnigma(99L, enigma, redirectAttributes);

    //     verify(redirectAttributes).addFlashAttribute("errorMessage", "❌ Fase no encontrada.");
    //     assertEquals("redirect:/error", result);
    // }

    // ----------------------------------------------------------
    // addEnigma - WITHOUT STATEMENT
    // ----------------------------------------------------------
    // @Test
    // void testAddEnigma_WithoutStatement() {
    //     Game game = new Game();
    //     game.setId(1L);
    //     Phase phase = new Phase();
    //     phase.setGame(game);
    //     when(phaseService.findPhaseById(5L)).thenReturn(phase);

    //     Enigma enigma = new Enigma(); // sin statement

    //     String result = enigmaController.addEnigma(5L, enigma, redirectAttributes);

    //     verify(redirectAttributes).addFlashAttribute("errorMessage", "❌ Debes introducir un nombre para la fase.");
    //     assertEquals("redirect:/editGame/1", result);
    // }
}
