package com.code.paratour.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code.paratour.model.Game;
import com.code.paratour.repositories.GameRepository;

/**
 * Service layer for managing {@link Game} entities.
 * 
 * This class provides business logic and a clean interface for accessing
 * and manipulating game data. It acts as an abstraction over the
 * {@link GameRepository}, centralizing all CRUD operations related to games.
 */
@Service
public class GameService {

    private final GameRepository gameRepository;

    /**
     * Constructs the service and injects the {@link GameRepository}.
     * 
     * @param gameRepository the repository responsible for game persistence operations
     */
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /**
     * Retrieves all games stored in the database.
     * 
     * @return a list containing all {@link Game} entities
     */
    public List<Game> findAllGames() {
        return gameRepository.findAll();
    }

    /**
     * Finds a specific game by its unique identifier.
     * 
     * @param idGame the ID of the game to retrieve
     * @return the corresponding {@link Game} entity, or {@code null} if not found
     */
    public Game findGameById(Long idGame) {
        return gameRepository.findById(idGame).orElse(null);
    }

    /**
     * Saves or updates a game entity in the database.
     * 
     * If the game already exists, it will be updated;
     * otherwise, a new record will be inserted.
     * 
     * @param game the {@link Game} entity to save
     * @return the persisted {@link Game} entity
     */
    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    /**
     * Deletes a game from the database by its unique identifier.
     * 
     * @param idGame the ID of the game to delete
     */
    public void deleteGame(Long idGame) {
        gameRepository.deleteById(idGame);
    }
}
