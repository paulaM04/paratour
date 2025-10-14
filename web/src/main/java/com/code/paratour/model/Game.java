package com.code.paratour.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "juegos")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_juego")
    private String gameType;

    @Column(name = "nombre")
    private String name;

    @Column(name = "descripcion")
    private String description;

    @Column(name = "imagen")
    private String image;

    private String video;

    @Column(name = "numero_enigmas")
    private Integer numberOfRiddles;

    @Column(name = "tiene_leaderboard")
    private Boolean hasLeaderboard;

@OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Phase> phases = new ArrayList<>();



    private Boolean manual;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
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

    public int getNumberOfRiddles() {
        return numberOfRiddles;
    }

    public void setNumberOfRiddles(int numberOfRiddles) {
        this.numberOfRiddles = numberOfRiddles;
    }

    public boolean isHasLeaderboard() {
        return hasLeaderboard;
    }

    public void setHasLeaderboard(boolean hasLeaderboard) {
        this.hasLeaderboard = hasLeaderboard;
    }

    public boolean getManual() {
        return manual;
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }

    public Game orElseThrow(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }

    public List<Phase> getPhases() {
        return phases;
    }

    public void setPhases(List<Phase> phases) {
        this.phases = phases;
    }

    public void addPhase(Phase phase) {
        phases.add(phase);
        phase.setGame(this);
    }

    public void deletePhase(Phase phase) {
        for (Enigma enigma : phase.getEnigmas()) {
            enigma.setPhase(null);
            enigma.setGame(null);
        }
        phases.remove(phase.getIdFalse());
    }

}
