package com.code.paratour.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @Column(name = "id_ticket")
    private Long ticketId;

    @Column(length = 255)
    private String email;

    @Column(name = "id_juego")
    private Integer gameId;

    @Column(name = "is_started")
    private Boolean started;

    @Column(name = "numero_ticket", columnDefinition = "TEXT")
    private String ticketNumber;

    @Column(name = "codigo_juego", length = 350)
    private String gameCode;

    @Column(name = "id_instancia")
    private Long instanceId;

    @Column(name = "num_jugadores")
    private Integer players;

    @Column(name = "num_jugadores_registrados")
    private Integer registeredPlayers;

    private Float amount;

    private Boolean active;

    @Column(name = "fecha_alta")
    private LocalDateTime createdAt;

    // getters and setters
}
