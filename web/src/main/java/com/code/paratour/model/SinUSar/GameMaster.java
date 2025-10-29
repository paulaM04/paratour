package com.code.paratour.model.SinUSar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
