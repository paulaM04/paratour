package com.code.paratour.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.code.paratour.repositories.GameRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

    @Autowired
    private GameRepository gameRepository;

    @PostConstruct
    public void init() throws IOException {

    }

}
