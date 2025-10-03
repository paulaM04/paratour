package com.code.paratour.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "progreso")
public class Progress {

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

    @Column(name = "id_fase")
    private Long phaseId;

    @Column(name = "id_enigma")
    private Long enigmaId;

    private Integer points;

    private Long time;

    @Column(name = "fecha_inicio")
    private LocalDate startDate;

    @Column(name = "fecha_completado")
    private LocalDate completedDate;

    // getters and setters
}
