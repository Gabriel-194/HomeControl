package com.pi.HomeControl.dto;

import lombok.Data;

@Data
public class cadastroDto {
    // Passo 1
    private String nome;
    private String email;
    private String telefone;
    private String senha;

    // Passo 2 - Decisão
    private String tipoCadastro; // "morador" ou "sindico"

    // Se for Morador
    private String codigoCondominio;
    private String codigoUnidade;

    // Se for Síndico (Criação do Condomínio)
    private String nomeCondominio;
    private String cnpj;
    private String endereco;
}