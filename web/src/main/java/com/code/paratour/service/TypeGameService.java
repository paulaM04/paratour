package com.code.paratour.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code.paratour.model.GameType;
import com.code.paratour.repositories.TypeGameRepository;

/**
 * Service layer for managing {@link GameType} entities.
 * 
 * This class provides business logic for retrieving and handling game types.
 * It acts as an abstraction layer between controllers and the
 * {@link TypeGameRepository}, centralizing all data access related to game types.
 */
@Service
public class TypeGameService {

    private final TypeGameRepository typeGameRepository;

    /**
     * Constructs the service and injects the {@link TypeGameRepository}.
     * 
     * @param typeGameRepository the repository used for game type persistence operations
     */
    public TypeGameService(TypeGameRepository typeGameRepository) {
        this.typeGameRepository = typeGameRepository;
    }

    /**
     * Retrieves all available game types from the database.
     * 
     * @return a list containing all {@link GameType} entities
     */
    public List<GameType> findAll() {
        return typeGameRepository.findAll();
    }

    /**
     * Finds a game type by its unique code.
     * 
     * @param code the code used to identify the game type
     * @return the matching {@link GameType} entity, or {@code null} if not found
     */
    public GameType findByCode(String code) {
        return typeGameRepository.findById(code).orElse(null);
    }

    /**
     * Finds a game type by its name.
     * 
     * @param name the name of the game type to search for
     * @return the matching {@link GameType} entity, or {@code null} if not found
     */
    public GameType findByName(String name) {
        return typeGameRepository.findByName(name);
    }
}
