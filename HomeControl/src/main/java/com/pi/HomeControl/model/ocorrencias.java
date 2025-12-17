package com.pi.HomeControl.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ocorrencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ocorrencias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    private String titulo;
    private String descricao;
    private String status;
    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "id_condominio")
    private condominio condominio;
}
