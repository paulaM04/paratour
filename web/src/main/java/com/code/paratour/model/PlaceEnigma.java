package com.code.paratour.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lugares_enigmas")
public class PlaceEnigma {

    @Id
    private Long id;

    @Column(name = "lugar_id")
    private Long placeId;

    @Column(name = "enigma_id")
    private Long enigmaId;

    private Boolean manual;

    // getters and setters
}
