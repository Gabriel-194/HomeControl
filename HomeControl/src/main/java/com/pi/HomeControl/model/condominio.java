package com.pi.HomeControl.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "condominio", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class condominio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_condominio")
    private Long id;

    private String nome;
    private String email;

    @Column(name = "schema_name", unique = true, nullable = false)
    private String schemaName;

    private Boolean ativo = true;


    @Column(name = "codigo_acesso", unique = true)
    private String codigoAcesso;
}