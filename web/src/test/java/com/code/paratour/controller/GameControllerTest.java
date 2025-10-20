package com.code.paratour.controller;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

public class GameControllerTest {

    @InjectMocks
    private GameController gameController;

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
    // HOME
    // ----------------------------------------------------------
    @Test
    void testHome_ReturnsHomeTemplate() {
        List<Game> games = List.of(new Game());
        when(gameService.findAllGames()).thenReturn(games);

        String view = gameController.home(model);

        verify(model).addAttribute("games", games);
        assertEquals("home", view);
    }

    // ----------------------------------------------------------
    // VIEW GAME
    // ----------------------------------------------------------
    @Test
    void testViewGame_WhenGameExists_ReturnsGameView() {
        Game game = new Game();
        game.setId(1L);
        Phase phase = new Phase();
        phase.setPhaseName("Phase 1");
        Enigma enigma = new Enigma();
        enigma.setId(3L);
        phase.setEnigmas(Set.of(enigma));
        game.setPhases(Set.of(phase));

        when(gameService.findGameById(1L)).thenReturn(game);

        String view = gameController.viewGame(1L, model);

        verify(model).addAttribute(eq("game"), any(Game.class));
        verify(model).addAttribute(eq("phases"), any());
        assertEquals("gameView", view);
    }

    @Test
    void testViewGame_WhenGameNotFound_ThrowsException() {
        when(gameService.findGameById(1L)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> gameController.viewGame(1L, model));
    }

    // ----------------------------------------------------------
    // EDIT GAME FORM
    // ----------------------------------------------------------
    @Test
    void testEditGameForm_ReturnsEditGameTemplate() {
        Game game = new Game();
        game.setId(1L);
        Phase phase = new Phase();
        Enigma enigma = new Enigma();
        enigma.setId(1L);
        phase.setEnigmas(Set.of(enigma));
        game.setPhases(Set.of(phase));

        GameType type = new GameType();
        type.setCode("type1");
        type.setName("Adventure");

        when(gameService.findGameById(1L)).thenReturn(game);
        when(typeGameService.findAll()).thenReturn(Set.of(type));

        String view = gameController.editGameForm(1L, model);

        verify(model).addAttribute(eq("game"), eq(game));
        verify(model).addAttribute(eq("typesGame"), any());
        assertEquals("editGame", view);
    }

    @Test
    void testEditGameForm_WhenGameNotFound_ThrowsException() {
        when(gameService.findGameById(1L)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> gameController.editGameForm(1L, model));
    }

}
