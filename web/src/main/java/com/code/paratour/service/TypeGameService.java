package com.code.paratour.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code.paratour.model.GameType;
import com.code.paratour.repositories.TypeGameRepository;

@Service
public class TypeGameService {

    private final TypeGameRepository typeGameRepository;

    public TypeGameService(TypeGameRepository typeGameRepository) {
        this.typeGameRepository = typeGameRepository;
    }

    public List<GameType> findAll() {
        return typeGameRepository.findAll();
    }

    public GameType findByCode(String code) {
        return typeGameRepository.findById(code).orElse(null);
    }

    public GameType findByName(String name) {
        return typeGameRepository.findByName(name);
    }

}
