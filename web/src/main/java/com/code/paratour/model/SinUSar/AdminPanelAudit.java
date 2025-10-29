package com.code.paratour.model.SinUSar;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auditoria_panel_admin")
public class AdminPanelAudit {

    @Id
    private Long id;

    @Column(name = "id_usuario_admin_panel")
    private Long adminUserId;

    @Column(name = "id_tipo_mensaje", length = 20)
    private String messageTypeId;

    @Column(columnDefinition = "TEXT")
    private String event;

    private LocalDateTime date;

    // getters and setters
}
