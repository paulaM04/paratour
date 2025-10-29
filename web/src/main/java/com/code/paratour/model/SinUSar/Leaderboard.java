package com.code.paratour.model.SinUSar;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "leaderboard")
public class Leaderboard {

    @Id
    private Long id;

    @Column(name = "id_de_usuario_invitado", length = 255)
    private String guestUserId;

    @Column(name = "nombre_de_usuario_invitado", length = 255)
    private String guestUserName;

    @Column(length = 255)
    private String email;

    @Column(name = "id_ticket")
    private Long ticketId;

    @Column(name = "id_juego")
    private Long gameId;

    @Column(name = "id_instancia")
    private Long instanceId;

    @Column(name = "puntuacion_total")
    private Integer totalScore;

    private LocalDate date;

    // getters and setters
}
