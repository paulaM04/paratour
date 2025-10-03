package com.code.paratour.model;

import jakarta.persistence.*;

@Entity
@Table(name = "grupos_instancias")
public class ClientGroup {

    @Id
    @Column(name = "id_ticket")
    private Long ticketId;

    @Column(name = "id_de_usuario_invitado", length = 255)
    private String guestUserId;

    @Column(name = "nombre_de_usuario_invitado", length = 255)
    private String guestUserName;

    @Column(length = 255)
    private String email;

    @Column(name = "nombre_grupo", length = 150)
    private String groupName;

    private Integer points;

    private Boolean active;

    // getters and setters
}
