package com.code.paratour.model;

import jakarta.persistence.*;

@Entity
@Table(name = "gamemaster")
public class GameMaster {

    @Id
    private Long id;

    @Column(length = 350)
    private String name;

    @Column(name = "num_telefono", length = 15)
    private String phoneNumber;

    private Boolean active;

    // getters and setters
}
