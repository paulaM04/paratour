package com.code.paratour.model;

import jakarta.persistence.*;

@Entity
@Table(name = "configuracion_clientes")
public class ClientConfig {

    @Id
    private Long id;

    @Column(name = "id_cliente", length = 50)
    private String clientId;

    @Column(length = 350)
    private String logo;

    @Column(name = "color_primario", length = 25)
    private String primaryColor;

    @Column(name = "color_secundario", length = 25)
    private String secondaryColor;

    @Column(name = "nombre_comercial", length = 100)
    private String commercialName;

    @Column(length = 150)
    private String claim;

    @Column(name = "carpeta_imgs", length = 500)
    private String imageFolder;

    // getters and setters
}
