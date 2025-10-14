package com.code.paratour.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code.paratour.model.Enigma;
import com.code.paratour.repositories.EnigmaRepository;

/**
 * Service layer for managing {@link Enigma} entities.
 * 
 * This class encapsulates business logic related to enigmas (riddles)
 * and provides an abstraction over the {@link EnigmaRepository}.
 * It centralizes all CRUD operations and interactions with the persistence layer.
 */
@Service
public class EnigmaService {

    private final EnigmaRepository enigmaRepository;

    /**
     * Constructs the service and injects the {@link EnigmaRepository}.
     * 
     * @param enigmaRepository the repository used for Enigma persistence operations
     */
    public EnigmaService(EnigmaRepository enigmaRepository) {
        this.enigmaRepository = enigmaRepository;
    }

    /**
     * Retrieves all enigmas stored in the database.
     * 
     * @return a list containing all {@link Enigma} entities
     */
    public List<Enigma> findAllGames() {
        return enigmaRepository.findAll();
    }

    /**
     * Finds a specific enigma by its unique identifier.
     * 
     * @param id the ID of the enigma to retrieve
     * @return the matching {@link Enigma} entity, or {@code null} if not found
     */
    public Enigma findEnigmaById(Long id) {
        return enigmaRepository.findById(id).orElse(null);
    }

    /**
     * Saves or updates an enigma entity in the database.
     * 
     * If the enigma already exists, it will be updated;
     * otherwise, a new record will be inserted.
     * 
     * @param enigma the {@link Enigma} entity to save
     * @return the persisted {@link Enigma} entity
     */
    public Enigma save(Enigma enigma) {
        return enigmaRepository.save(enigma);
    }

    /**
     * Deletes an enigma from the database by its ID.
     * 
     * @param idPhase the ID of the enigma to delete
     */
    public void delete(Long idPhase) {
        enigmaRepository.deleteById(idPhase);
    }

    public void deleteAll(List<Enigma> enigmas) {
        enigmaRepository.deleteAll(enigmas);
    }

    
}
