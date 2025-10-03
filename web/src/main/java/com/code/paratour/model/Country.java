package com.code.paratour.model;

import jakarta.persistence.*;

@Entity
@Table(name = "paises")
public class Country {

    @Id
    @Column(name = "iso_alpha2", length = 2)
    private String isoAlpha2;

    @Column(name = "iso_alpha3", length = 3)
    private String isoAlpha3;

    @Column(length = 150)
    private String name;

    // getters and setters
}
