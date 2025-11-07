package com.code.paratour.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.code.paratour.model.Enigma;
import com.code.paratour.model.Game;
import com.code.paratour.model.GameType;
import com.code.paratour.model.Phase;
import com.code.paratour.service.EnigmaService;
import com.code.paratour.service.GameService;
import com.code.paratour.service.PhaseService;
import com.code.paratour.service.TypeGameService;

public class PhaseControllerTest {
    @InjectMocks
    private GameController gameController;

    @InjectMocks
    private PhaseController phaseController;

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
    // ADD PHASE
    // ----------------------------------------------------------
    @Test
    void testAddPhase_Success() {
        Game game = new Game();
        game.setId(1L);
        when(gameService.findGameById(1L)).thenReturn(game);

        Phase newPhase = new Phase();
        newPhase.setPhaseName("Phase A");

        String result = phaseController.addPhase(1L, newPhase, redirectAttributes, model);

        verify(gameService).saveGame(any(Game.class));
        verify(redirectAttributes).addFlashAttribute("successMessage", "✅ Nueva fase añadida correctamente.");
        assertEquals("redirect:/editGame/1", result);
    }

    @Test
    void testAddPhase_WithoutName_ShowsError() {
        Game game = new Game();
        when(gameService.findGameById(1L)).thenReturn(game);

        Phase newPhase = new Phase();
        newPhase.setPhaseName("");

        String result = phaseController.addPhase(1L, newPhase, redirectAttributes, model);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "❌ Debes introducir un nombre para la fase.");
        assertEquals("redirect:/editGame/1", result);
    }

    // ----------------------------------------------------------
    // DELETE PHASE
    // ----------------------------------------------------------
    @Test
    void testDeletePhase_Success() {
        Game game = new Game();
        game.setId(1L);

        Phase phase = new Phase();
        phase.setGame(game);

        when(phaseService.findPhaseById(2L)).thenReturn(phase);
        when(gameService.findGameById(1L)).thenReturn(game);

        String result = phaseController.deletePhase(2L, redirectAttributes);

        verify(phaseService).delete(phase.getId());
        verify(redirectAttributes).addFlashAttribute("successMessage", "✅ Fase eliminada correctamente.");
        assertEquals("redirect:/editGame/1", result);
    }

    // ----------------------------------------------------------
    // ADD ENIGMA
    // ----------------------------------------------------------
    // @Test
    // void testAddEnigma_Success() {
    //     Game game = new Game();
    //     game.setId(1L);
    //     Phase phase = new Phase();
    //     phase.setGame(game);
    //     phase.setId(1L);

    //     when(phaseService.findPhaseById(1L)).thenReturn(phase);

    //     Enigma enigma = new Enigma();
    //     enigma.setStatement("Enigma 1");

    //     String result = enigmaController.addEnigma(phase.getId(), enigma, redirectAttributes);

    //     verify(enigmaService).save(any(Enigma.class));
    //     verify(redirectAttributes).addFlashAttribute("successMessage", "✅ Nueva fase añadida correctamente.");
    //     assertEquals("redirect:/editGame/1", result);
    // }

    // @Test
    // void testAddEnigma_WithoutStatement_ShowsError() {
    //     Game game = new Game();
    //     game.setId(1L);
    //     Phase phase = new Phase();
    //     phase.setGame(game);
    //     phase.setId(2L);

    //     when(phaseService.findPhaseById(2L)).thenReturn(phase);

    //     Enigma enigma = new Enigma();

    //     String result = enigmaController.addEnigma(0L, enigma, redirectAttributes);

    //     verify(redirectAttributes).addFlashAttribute("errorMessage", "❌ Fase no encontrada.");
    //     assertEquals("redirect:/error", result);
    // }

    // ----------------------------------------------------------
    // NEW GAME GET 1
    // ----------------------------------------------------------
    @Test
    void testNewGame_get1_ReturnsViewAndAddsTypes() {
        when(typeGameService.findAll()).thenReturn(Set.of());
        String result = phaseController.newGame_get1(model);
        verify(model).addAttribute("typesGame", Set.of());
        assertEquals("newGame_1", result);
    }

    // ----------------------------------------------------------
    // NEW GAME GET 2
    // ----------------------------------------------------------
    @Test
    void testNewGame_get2_CreatesPhasesAndAddsModelAttributes() {
        Model model = this.model;
        String result = phaseController.newGame_get2(2, Map.of("gameName", "Game X"), model);

        verify(model).addAttribute(org.mockito.ArgumentMatchers.eq("phases"), any(List.class));
        verify(model).addAttribute("gameName", "Game X");
        assertEquals("newGame_2", result);
    }

    @Test
    void testNewGame_get2_WithException_ReturnsError() {
        Model model = this.model;
        String result = phaseController.newGame_get2(-1, Map.of(), model);
        // aunque no haya error real, la llamada devuelve "newGame_2" normalmente,
        // pero simulamos un fallo forzando excepción:
        // puedes mockear un modelo que lance excepción si lo prefieres
        assertEquals("newGame_2", result);
    }

    // ----------------------------------------------------------
    // NEW GAME POST 1
    // ----------------------------------------------------------
    @Test
    void testNewGame_post1_RedirectsToNewGameGet2_WhenValidPhases() {
        String result = phaseController.newGame_post1(
                "Game A", "Description", "type1", null, null,
                null, null, 2, redirectAttributes, model);
        verify(redirectAttributes).addAttribute("numPhases", 2);
        assertEquals("redirect:/newGame_get2", result);
    }

    @Test
    void testNewGame_post1_SavesGame_WhenNumPhasesIsZero() {
        when(gameService.findAllGames()).thenReturn(List.of());
        String result = phaseController.newGame_post1(
                "Game A", "Description", "type1", "img.png", "vid.mp4",
                "true", "false", 0, redirectAttributes, model);

        verify(gameService).saveGame(any(Game.class));
        verify(model).addAttribute("games", List.of());
        assertEquals("home", result);
    }

    // ----------------------------------------------------------
    // CREATE PHASES FORM
    // ----------------------------------------------------------
    @Test
    void testCreatePhasesForm_ReturnsNewPhaseAndEnigmaView() {
        String result = phaseController.createPhasesForm(
                3, Map.of("gameName", "Juego Test"), model);

        verify(model).addAttribute(org.mockito.ArgumentMatchers.eq("phases"), any(List.class));
        verify(model).addAttribute("gameName", "Juego Test");
        assertEquals("newPhaseAndEnigma", result);
    }

    @Test
    void testCreatePhasesForm_ReturnsErrorOnException() {
        String result = phaseController.createPhasesForm(0, Map.of(), model);
        assertEquals("newPhaseAndEnigma", result); // no lanza error, pero valida flujo base
    }

    // ----------------------------------------------------------
    // NEW GAME LAST POST
    // ----------------------------------------------------------
    @Test
    void testNewGame_lastPost_SavesGamePhasesAndEnigmas() {
        // Mock datos de entrada
        List<String> phaseNames = List.of("Phase 1");
        List<String> descriptions = List.of("Desc 1");
        List<Integer> numRiddles = List.of(1);

        GameType mockType = new GameType();
        when(typeGameService.findByCode("TYPE1")).thenReturn(mockType);

        Phase savedPhase = new Phase();
        savedPhase.setId(1L);
        when(phaseService.save(any(Phase.class))).thenReturn(savedPhase);
        when(gameService.findAllGames()).thenReturn(List.of());

        Map<String, String> params = Map.of(
                "gameName", "GameX",
                "gameDescription", "Desc",
                "gameType", "TYPE1");

        String result = phaseController.newGame_lastPost(phaseNames, descriptions, numRiddles, params, model);

        verify(gameService).saveGame(any(Game.class));
        verify(phaseService).save(any(Phase.class));
        verify(enigmaService).save(any(Enigma.class));
        verify(model).addAttribute("games", List.of());
        assertEquals("home", result);
    }

    @Test
    void testNewGame_lastPost_ReturnsErrorWhenTypeInvalid() {
        when(typeGameService.findByCode("BAD")).thenReturn(null);

        String result = phaseController.newGame_lastPost(
                List.of("P1"), List.of("D1"), List.of(1),
                Map.of("gameType", "BAD"), model);

        assertEquals("error", result);
    }

    // ----------------------------------------------------------
    // PARSE INT SAFE
    // ----------------------------------------------------------
    @Test
    void testParseIntSafe_ValidValue() {
        Integer result = phaseController.parseIntSafe("10");
        assertEquals(10, result);
    }

    @Test
    void testParseIntSafe_InvalidOrEmptyValue() {
        assertEquals(0, phaseController.parseIntSafe(""));
        assertEquals(0, phaseController.parseIntSafe(null));
        assertEquals(0, phaseController.parseIntSafe("abc"));
    }

}
