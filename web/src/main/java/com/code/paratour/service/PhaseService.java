package com.code.paratour.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code.paratour.model.Phase;
import com.code.paratour.repositories.PhaseRepository;

@Service

public class PhaseService {
    private final PhaseRepository phaseRepository;

    public PhaseService(PhaseRepository gameRepository) {
        this.phaseRepository = gameRepository;
    }

    public List<Phase> findAllPhases() {
        return phaseRepository.findAll();
    }

    public Phase findPhaseById(Long id) {
        return phaseRepository.findById(id).orElse(null);
    }

    public Phase save(Phase game) {
        return phaseRepository.save(game);
    }

    public void delete(Long idPhase) {
        phaseRepository.deleteById(idPhase);
    }

    public List<Phase> findByGameId(Long gameId) {
        return phaseRepository.findByGameId(gameId);
    }

}