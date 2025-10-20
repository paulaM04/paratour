package com.code.paratour.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code.paratour.model.Phase;

public interface PhaseRepository extends JpaRepository<Phase, Long> {
     Set<Phase> findByGameId(Long gameId);
}
