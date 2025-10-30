package com.code.paratour.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code.paratour.model.GameType;

/**
 * Repository interface for managing {@link GameType} entities.
 * 
 * This repository provides CRUD operations and additional
 * query methods for accessing game type data. It leverages
 * Spring Data JPA’s automatic query derivation mechanism.
 */
public interface TypeGameRepository extends JpaRepository<GameType, String> {

    /**
     * Retrieves all existing game types from the database.
     * 
     * Although {@link JpaRepository} already provides a default implementation
     * of this method, explicitly declaring it can improve clarity or support
     * future customization.
     * 
     * @return a list containing all {@link GameType} entities
     */
    List<GameType> findAll();

    /**
     * Finds a game type entity by its unique code.
     * 
     * @param code the code that identifies the game type
     * @return the {@link GameType} entity with the matching code, or {@code null} if not found
     */
    GameType findByCode(String code);

    /**
     * Finds a game type entity by its name.
     * 
     * @param name the name of the game type
     * @return the {@link GameType} entity with the matching name, or {@code null} if not found
     */
    GameType findByName(String name);
}
