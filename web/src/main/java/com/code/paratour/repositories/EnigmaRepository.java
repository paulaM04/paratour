package com.code.paratour.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code.paratour.model.Enigma;

public interface EnigmaRepository extends JpaRepository<Enigma, Long> {
        List<Enigma> findByPhaseName(Long phase);

}
