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

    @Column(name = "nombre", length = 300)
    private String name;

    @Column(name = "usuario", length = 300)
    private String username;

    @Column(name = "password", columnDefinition = "TEXT")
    private String password;

    @Column(name = "perfil", length = 75)
    private String role;

    @Column(name = "foto", length = 350)
    private String photo;

    @Column(name = "estado")
    private Boolean status;

    @Column(name = "ultimo_login")
    private LocalDateTime lastLogin;

    // Getters y setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
}
