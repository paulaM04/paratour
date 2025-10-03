package com.code.paratour.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipos_de_juego")
public class GameType {

    @Id
    @Column(name = "codigo", length = 20)   // 👈 antes tenías "code"
    private String code;

    @Column(name = "nombre", length = 120)  // 👈 antes tenías "name"
    private String name;

    @Column(name = "descripcion", length = 350)
    private String description;

    // getters y setters
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
