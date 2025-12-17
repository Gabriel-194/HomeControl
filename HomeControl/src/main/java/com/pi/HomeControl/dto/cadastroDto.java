package com.pi.HomeControl.dto;

import lombok.Data;

@Data
public class cadastroDto {
    private String nome;
    private String email;
    private String telefone;
    private String senha;
    private String tipoCadastro;
    private String codigoCondominio;
    private String codigoUnidade;
    private String nomeCondominio;
    private String cnpj;
    private String endereco;
}