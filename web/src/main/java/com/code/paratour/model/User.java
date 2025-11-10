package com.code.paratour.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Column(name = "nombre", length = 150)
    private String firstName;

    @Column(name = "apellidos", length = 150)
    private String lastName;

    @Column(name = "nick", length = 150)
    private String nick;

    @Column(name = "lead_id_upviral", length = 80)
    private String upViralLeadId;

    @Column(name = "referal_link_upviral", columnDefinition = "TEXT")
    private String referralLinkUpViral;

    @Column(name = "activo")
    private Boolean active;

    @Column(name = "otp")
    private Integer otp;

    @Column(name = "otp_expires")
    private Long otpExpires;

    // --- Getters y Setters ---

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getGuestUserId() { return guestUserId; }
    public void setGuestUserId(String guestUserId) { this.guestUserId = guestUserId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getNick() { return nick; }
    public void setNick(String nick) { this.nick = nick; }

    public String getUpViralLeadId() { return upViralLeadId; }
    public void setUpViralLeadId(String upViralLeadId) { this.upViralLeadId = upViralLeadId; }

    public String getReferralLinkUpViral() { return referralLinkUpViral; }
    public void setReferralLinkUpViral(String referralLinkUpViral) { this.referralLinkUpViral = referralLinkUpViral; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Integer getOtp() { return otp; }
    public void setOtp(Integer otp) { this.otp = otp; }

    public Long getOtpExpires() { return otpExpires; }
    public void setOtpExpires(Long otpExpires) { this.otpExpires = otpExpires; }
}
