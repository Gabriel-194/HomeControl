package com.pi.HomeControl.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class usuarioSessao {
    private Long id;
    private String nome;
    private String email;
    private String role;
    private Long idCondominio;
    private String schemaName;
}