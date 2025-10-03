package com.code.paratour.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.code.paratour.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    Optional<Client> findByCodeClient(String codeClient);

    List<Client> findAll();

    //List<Client> findByDeleted(boolean deleted);

}
