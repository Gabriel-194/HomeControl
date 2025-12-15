package com.pi.HomeControl.dto;

import lombok.Data;

@Data
public class moradoresResponseDto {
    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private String bloco;
    private Integer unidade;
    private String perfil;
    private Boolean ativo;
}