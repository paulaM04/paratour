package com.code.paratour.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code.paratour.model.Phase;

public interface PhaseRepository extends JpaRepository<Phase, Long> {
     List<Phase> findByGameId(Long gameId);
}
