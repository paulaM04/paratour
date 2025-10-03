package com.code.paratour.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios_panel_admin")
public class AdminUser {

    @Id
    private Long id;

    @Column(length = 300)
    private String name;

    @Column(length = 300)
    private String username;

    @Column(columnDefinition = "TEXT")
    private String password;

    @Column(length = 75)
    private String role;

    @Column(length = 350)
    private String photo;

    private Boolean status;

    @Column(name = "ultimo_login")
    private LocalDateTime lastLogin;

    // getters and setters
}
