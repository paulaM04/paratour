package com.code.paratour.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code.paratour.model.Enigma;
import com.code.paratour.repositories.EnigmaRepository;

@Service
public class EnigmaService {
    private final EnigmaRepository enigmaRepository;

    public EnigmaService(EnigmaRepository enigmaRepository) {
        this.enigmaRepository = enigmaRepository;
    }

    public List<Enigma> findAllGames() {
        return enigmaRepository.findAll();
    }

    public Enigma findEnigmaById(Long id) {
        return enigmaRepository.findById(id).orElse(null);
    }

    public Enigma save(Enigma enigma) {
        return enigmaRepository.save(enigma);
    }

    public void delete(Long idPhase) {
        enigmaRepository.deleteById(idPhase);
    }



}
