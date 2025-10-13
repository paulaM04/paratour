package com.code.paratour.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code.paratour.model.Client;
import com.code.paratour.repositories.ClientRepository;

/**
 * Service layer for managing {@link Client} entities.
 * 
 * This class encapsulates business logic and provides a clean interface
 * between controllers and the {@link ClientRepository}. It handles
 * CRUD operations and ensures repository access is centralized.
 */
@Service
public class ClientService {

    private final ClientRepository repository;

    /**
     * Constructs the service and injects the client repository.
     * 
     * @param repository the repository used for data persistence operations
     */
    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all clients from the database.
     * 
     * @return a list containing all {@link Client} entities
     */
    public List<Client> getAllClientes() {
        return repository.findAll();
    }

    /**
     * Retrieves a client by its unique identifier.
     * 
     * @param codigoCliente the unique code of the client
     * @return the corresponding {@link Client} entity, or {@code null} if not found
     */
    public Client getClienteById(String codigoCliente) {
        return repository.findById(codigoCliente).orElse(null);
    }

    /**
     * Saves or updates a client entity.
     * 
     * If the client already exists (matching ID), it will be updated.
     * Otherwise, a new record will be created.
     * 
     * @param cliente the {@link Client} entity to be persisted
     * @return the saved or updated {@link Client} entity
     */
    public Client saveCliente(Client cliente) {
        return repository.save(cliente);
    }

    /**
     * Deletes a client from the database by its unique identifier.
     * 
     * @param codigoCliente the unique code of the client to delete
     */
    public void deleteCliente(String codigoCliente) {
        repository.deleteById(codigoCliente);
    }
}
