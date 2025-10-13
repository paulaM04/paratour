package com.code.paratour.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.code.paratour.model.Client;

/**
 * Repository interface for the {@link Client} entity.
 * 
 * This interface extends {@link JpaRepository}, providing CRUD operations
 * and query methods for the Client table. Custom query methods can also be
 * declared here using Spring Data JPA's naming conventions.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    /**
     * Finds a single client by its unique code.
     * 
     * @param codeClient the client code to search for
     * @return an {@link Optional} containing the client if found, or empty otherwise
     */
    Optional<Client> findByCodeClient(String codeClient);

    /**
     * Retrieves all clients from the database.
     * Although JpaRepository already provides this method by default,
     * explicitly declaring it can improve readability or allow for future overrides.
     * 
     * @return a list of all clients
     */
    List<Client> findAll();
}
