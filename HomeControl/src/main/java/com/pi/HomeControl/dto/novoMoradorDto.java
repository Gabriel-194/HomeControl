package com.pi.HomeControl.dto;

import lombok.Data;

@Data
public class novoMoradorDto {
    private String nome;
    private String email;
    private String telefone;
    private String nomeBloco;
    private Integer numeroUnidade;
    private String perfil;
}