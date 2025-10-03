package com.code.paratour.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.code.paratour.model.GameType;
import com.code.paratour.repositories.GameTypeRepository;

@Service
public class GameTypeService {

    @Autowired
    private GameTypeRepository repo;

    public List<GameType> findAll() {
        return repo.findAll();
    }

    public boolean existsByCode(String code) {
        return repo.existsByCode(code);
    }
}
