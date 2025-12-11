package com.pi.HomeControl.dto;

import lombok.Data;

@Data
public class novoMoradorDto {
    private String nome;
    private String email;
    private String telefone;
    private String nomeBloco;   // Ex: "Bloco A"
    private Integer numeroUnidade; // Ex: 101
    private String perfil;      // "Morador", "Síndico", etc.
}