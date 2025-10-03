package com.code.paratour.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code.paratour.model.GameType;

public interface GameTypeRepository extends JpaRepository<GameType, String> {
    boolean existsByCode(String code);
}
