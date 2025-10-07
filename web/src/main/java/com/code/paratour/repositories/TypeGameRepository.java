package com.code.paratour.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code.paratour.model.GameType;

public interface TypeGameRepository extends JpaRepository<GameType, String>{

    public List<GameType> findAll();

}
