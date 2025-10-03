package com.code.paratour.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "fases")
public class Phase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ← Hibernate gestiona la FK
    @JoinColumn(name = "id_juego", nullable = true) // ← en tu DDL es NULLABLE; déjalo así si tienes fases “huérfanas”
    private Game game;

    @Column(name = "fase")
    private String phaseName;

    @Column(name = "literal_txt", length = 100)
    private String literalText;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;
    @Column(name = "imagen", columnDefinition = "TEXT")
    private String image;

    @Column(name = "video", columnDefinition = "TEXT")
    private String video;

    @Column(name = "latitud", length = 100)
    private String latitude;

    @Column(name = "longitud", length = 100)
    private String longitude;

    @Column(name = "proxima_fase")
    private Integer nextPhase;

    @Column(name = "url_mapa_fase", columnDefinition = "TEXT")
    private String mapUrl;

    @Column(name = "manual", nullable = false)
    private Boolean manual = Boolean.TRUE;

    @OneToMany(mappedBy = "phase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enigma> enigmas = new ArrayList<>();

    @Transient
    private int idFalse; // Para evitar que se muestre el ID en el formulario

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setEnigmas(List<Enigma> enigmas) {
        this.enigmas = enigmas;
    }

    public List<Enigma> getEnigmas() {
        return enigmas;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setIdFalse(int idFalse) {
        this.idFalse = idFalse;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public String getLiteralText() {
        return literalText;
    }

    public void setLiteralText(String literalText) {
        this.literalText = literalText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer getNextPhase() {
        return nextPhase;
    }

    public void setNextPhase(Integer nextPhase) {
        this.nextPhase = nextPhase;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public Boolean getManual() {
        return manual;
    }

    public void setManual(Boolean manual) {
        this.manual = manual;
    }

}
