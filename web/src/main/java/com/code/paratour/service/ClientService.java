package com.code.paratour.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code.paratour.model.Client;
import com.code.paratour.repositories.ClientRepository;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<Client> getAllClientes() {
        return repository.findAll();
    }

    public Client getClienteById(String codigoCliente) {
        return repository.findById(codigoCliente).orElse(null);
    }

    public Client saveCliente(Client cliente) {
        return repository.save(cliente);
    }

    public void deleteCliente(String codigoCliente) {
        repository.deleteById(codigoCliente);
    }
}
