package com.code.paratour.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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
