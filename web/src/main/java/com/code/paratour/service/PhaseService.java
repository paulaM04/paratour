package com.code.paratour.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code.paratour.model.Phase;
import com.code.paratour.repositories.PhaseRepository;

/**
 * Service layer for managing {@link Phase} entities.
 * 
 * This class provides business logic and data access abstraction
 * for game phases. It interacts with the {@link PhaseRepository}
 * to perform CRUD operations and retrieve phases associated with specific games.
 */
@Service
public class PhaseService {

    private final PhaseRepository phaseRepository;

    /**
     * Constructs the service and injects the {@link PhaseRepository}.
     * 
     * @param phaseRepository the repository responsible for phase persistence operations
     */
    public PhaseService(PhaseRepository phaseRepository) {
        this.phaseRepository = phaseRepository;
    }

    /**
     * Retrieves all phases stored in the database.
     * 
     * @return a list containing all {@link Phase} entities
     */
    public List<Phase> findAllPhases() {
        return phaseRepository.findAll();
    }

    /**
     * Finds a specific phase by its unique identifier.
     * 
     * @param id the ID of the phase to retrieve
     * @return the corresponding {@link Phase} entity, or {@code null} if not found
     */
    public Phase findPhaseById(Long id) {
        return phaseRepository.findById(id).orElse(null);
    }

    /**
     * Saves or updates a phase entity in the database.
     * 
     * If the phase already exists, it will be updated;
     * otherwise, a new record will be inserted.
     * 
     * @param phase the {@link Phase} entity to persist
     * @return the saved or updated {@link Phase} entity
     */
    public Phase save(Phase phase) {
        return phaseRepository.save(phase);
    }

    /**
     * Deletes a phase from the database by its unique identifier.
     * 
     * @param idPhase the ID of the phase to delete
     */
    public void delete(Long idPhase) {
        phaseRepository.deleteById(idPhase);
    }

    /**
     * Retrieves all phases associated with a specific game.
     * 
     * @param gameId the ID of the game whose phases are to be retrieved
     * @return a list of {@link Phase} entities related to the given game ID
     */
    public List<Phase> findByGameId(Long gameId) {
        return phaseRepository.findByGameId(gameId);
    }
}
