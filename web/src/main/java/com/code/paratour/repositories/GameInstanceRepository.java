package com.code.paratour.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code.paratour.model.GameInstance;

public interface GameInstanceRepository extends JpaRepository<GameInstance, Long> {
    List<GameInstance> findByGameId(Long gameId);
    void deleteAll(Iterable<? extends GameInstance> entities);
}

