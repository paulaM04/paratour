package com.code.paratour.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "enigmas")
public class Enigma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "fase", nullable = false)
private Long phaseId;

    @ManyToOne
    @JoinColumn(name = "fase", insertable = false, updatable = false)
    private Phase phase;

    @Column(name = "literal_txt", length = 100)
    private String literalText;

    @Column(name = "numero_enigma")
    private Integer enigmaNumber;

    @Column(name = "imagen", columnDefinition = "TEXT")
    private String image;

    @Column(name = "localizacion", columnDefinition = "TEXT")
    private String location;

    @Column(name = "introduccion", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "video_introduccion_avatar", columnDefinition = "TEXT")
    private String introAvatarVideo;

    @Column(name = "video_enigma", columnDefinition = "TEXT")
    private String enigmaVideo;

    @Column(columnDefinition = "TEXT")
    private String enigma;

    @Column(name = "formato_respuesta", columnDefinition = "TEXT")
    private String answerFormat;

    @Column(name = "puntos_acierto")
    private Integer pointsCorrect;

    @Column(name = "puntos_fallo")
    private Integer pointsFail;

    @Column(name = "puntos_pista1")
    private Integer pointsHint1;

    @Column(name = "puntos_pista2")
    private Integer pointsHint2;

    @Column(name = "pista1", columnDefinition = "TEXT")
    private String hint1;

    @Column(name = "pista2", columnDefinition = "TEXT")
    private String hint2;

    @Column(name = "respuesta", columnDefinition = "TEXT")
    private String answer;

    @Column(name = "explicacion_spot", columnDefinition = "TEXT")
    private String explanationSpot;

    @Column(name = "video_explicacion_spot", columnDefinition = "TEXT")
    private String explanationSpotVideo;

    @Column(name = "foto_localizacion_resolucion", columnDefinition = "TEXT")
    private String locationResolutionPhoto;

    @Column(name = "tiempo_maximo")
    private Integer maxTime;

    @Column(name = "latitud", length = 50)
    private String latitude;

    @Column(name = "longitud", length = 50)
    private String longitude;

    @Column(name = "instrucciones_adicionales", columnDefinition = "TEXT")
    private String additionalInstructions;
    
    @Transient
    private int idTreak; // Para evitar que se muestre el ID en el formulario
    private Boolean manual;


    // getters and setters
    public void setPhaseId(Long phaseId) {
        this.phaseId = phaseId;
    }

    public Long getIdPhase(){
        return this.phaseId;
    }
public Phase getPhase() {
        return phase;
    }
    public void setPhase(Phase phase) {
        this.phase = phase;
    }
    public void setIdidTreak(int idFalse) {
        this.idTreak = idFalse;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLiteralText() {
        return literalText;
    }

    public void setLiteralText(String literalText) {
        this.literalText = literalText;
    }

    public Integer getEnigmaNumber() {
        return enigmaNumber;
    }

    public void setEnigmaNumber(Integer enigmaNumber) {
        this.enigmaNumber = enigmaNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getIntroAvatarVideo() {
        return introAvatarVideo;
    }

    public void setIntroAvatarVideo(String introAvatarVideo) {
        this.introAvatarVideo = introAvatarVideo;
    }

    public String getEnigmaVideo() {
        return enigmaVideo;
    }

    public void setEnigmaVideo(String enigmaVideo) {
        this.enigmaVideo = enigmaVideo;
    }

    public String getEnigma() {
        return enigma;
    }

    public void setEnigma(String enigma) {
        this.enigma = enigma;
    }

    public String getAnswerFormat() {
        return answerFormat;
    }

    public void setAnswerFormat(String answerFormat) {
        this.answerFormat = answerFormat;
    }

    public Integer getPointsCorrect() {
        return pointsCorrect;
    }

    public void setPointsCorrect(Integer pointsCorrect) {
        this.pointsCorrect = pointsCorrect;
    }

    public Integer getPointsFail() {
        return pointsFail;
    }

    public void setPointsFail(Integer pointsFail) {
        this.pointsFail = pointsFail;
    }

    public Integer getPointsHint1() {
        return pointsHint1;
    }

    public void setPointsHint1(Integer pointsHint1) {
        this.pointsHint1 = pointsHint1;
    }

    public Integer getPointsHint2() {
        return pointsHint2;
    }

    public void setPointsHint2(Integer pointsHint2) {
        this.pointsHint2 = pointsHint2;
    }

    public String getHint1() {
        return hint1;
    }

    public void setHint1(String hint1) {
        this.hint1 = hint1;
    }

    public String getHint2() {
        return hint2;
    }

    public void setHint2(String hint2) {
        this.hint2 = hint2;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getExplanationSpot() {
        return explanationSpot;
    }

    public void setExplanationSpot(String explanationSpot) {
        this.explanationSpot = explanationSpot;
    }

    public String getExplanationSpotVideo() {
        return explanationSpotVideo;
    }

    public void setExplanationSpotVideo(String explanationSpotVideo) {
        this.explanationSpotVideo = explanationSpotVideo;
    }

    public String getLocationResolutionPhoto() {
        return locationResolutionPhoto;
    }

    public void setLocationResolutionPhoto(String locationResolutionPhoto) {
        this.locationResolutionPhoto = locationResolutionPhoto;
    }

    public Integer getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Integer maxTime) {
        this.maxTime = maxTime;
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

    public String getAdditionalInstructions() {
        return additionalInstructions;
    }

    public void setAdditionalInstructions(String additionalInstructions) {
        this.additionalInstructions = additionalInstructions;
    }

    public Boolean getManual() {
        return manual;
    }

    public void setManual(Boolean manual) {
        this.manual = manual;
    }
}
