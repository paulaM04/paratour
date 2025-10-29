package com.code.paratour.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "instancias_juegos")
public class GameInstance {

    @Id
    @Column(name = "id_instancia")
    private Long instanceId;

    @Column(name = "id_juego")
    private Long gameId;

    @Column(name = "nombre", length = 350)
    private String name;

    @Column(name = "programado")
    private Boolean scheduled;

    @Column(name = "id_gamemaster")
    private Long gameMasterId;

    @Column(name = "dia")
    private LocalDate day;

    @Column(name = "hora")
    private LocalTime hour;

    @Column(name = "activo")
    private Boolean active;

    @Column(name = "maximo_jugadores")
    private Integer maxPlayers;

    @Column(name = "precio_persona_item")
    private Float pricePerPerson;

    @Column(name = "upviral_campaign", length = 250)
    private String upViralCampaign;

    @Column(name = "manual")
    private Boolean manual;

    // Getters y setters
    public Long getInstanceId() { return instanceId; }
    public void setInstanceId(Long instanceId) { this.instanceId = instanceId; }

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getScheduled() { return scheduled; }
    public void setScheduled(Boolean scheduled) { this.scheduled = scheduled; }

    public Long getGameMasterId() { return gameMasterId; }
    public void setGameMasterId(Long gameMasterId) { this.gameMasterId = gameMasterId; }

    public LocalDate getDay() { return day; }
    public void setDay(LocalDate day) { this.day = day; }

    public LocalTime getHour() { return hour; }
    public void setHour(LocalTime hour) { this.hour = hour; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Integer getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(Integer maxPlayers) { this.maxPlayers = maxPlayers; }

    public Float getPricePerPerson() { return pricePerPerson; }
    public void setPricePerPerson(Float pricePerPerson) { this.pricePerPerson = pricePerPerson; }

    public String getUpViralCampaign() { return upViralCampaign; }
    public void setUpViralCampaign(String upViralCampaign) { this.upViralCampaign = upViralCampaign; }

    public Boolean getManual() { return manual; }
    public void setManual(Boolean manual) { this.manual = manual; }
}
