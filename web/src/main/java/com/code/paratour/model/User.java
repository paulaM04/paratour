package com.code.paratour.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @Column(length = 255)
    private String email;

    @Column(length = 200)
    private String password;

    @Column(name = "refresh_token", length = 250)
    private String refreshToken;

    @Column(name = "id_de_usuario_invitado", length = 255)
    private String guestUserId;

    @Column(length = 150)
    private String firstName;

    @Column(length = 150)
    private String lastName;

    @Column(length = 150)
    private String nick;

    @Column(name = "lead_id_upviral", length = 80)
    private String upViralLeadId;

    @Column(name = "referal_link_upviral", columnDefinition = "TEXT")
    private String referralLinkUpViral;

    private Boolean active;

    private Integer otp;

    @Column(name = "otp_expires")
    private Long otpExpires;

    // getters and setters
}
