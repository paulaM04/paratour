package com.code.paratour.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lugares")
public class Place {

    @Id
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(columnDefinition = "TEXT")
    private String text;

    private Boolean active;

    private Boolean manual;

    // getters and setters
}
