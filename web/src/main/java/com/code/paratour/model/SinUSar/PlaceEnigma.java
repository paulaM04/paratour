package com.code.paratour.model.SinUSar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
