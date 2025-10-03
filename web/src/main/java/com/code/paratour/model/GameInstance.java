package com.code.paratour.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "instancias_juegos")
public class GameInstance {

    @Id
    @Column(name = "id_instancia")
    private Long instanceId;

    @Column(name = "id_juego")
    private Long gameId;

    @Column(length = 350)
    private String name;

    private Boolean scheduled;

    @Column(name = "id_gamemaster")
    private Long gameMasterId;

    private LocalDate day;

    private LocalTime hour;

    private Boolean active;

    @Column(name = "maximo_jugadores")
    private Integer maxPlayers;

    @Column(name = "precio_persona_item")
    private Float pricePerPerson;

    @Column(name = "upviral_campaign", length = 250)
    private String upViralCampaign;

    private Boolean manual;

    // getters and setters
}
