package com.pi.HomeControl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class tenantService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void criarSchemaCondominio(String schemaName) {

        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);

        jdbcTemplate.execute(String.format("""
            CREATE TABLE %s.bloco (
                id_bloco SERIAL PRIMARY KEY,
                nome VARCHAR(255) NOT NULL,
                descricao TEXT,
                ativo BOOLEAN DEFAULT TRUE
            );
        """, schemaName));

        // Tabela UNIDADE (Ligada ao Bloco)
        jdbcTemplate.execute(String.format("""
            CREATE TABLE %s.unidade (
                id_unidade SERIAL PRIMARY KEY,
                id_bloco INT REFERENCES %s.bloco(id_bloco),
                numero INT NOT NULL,
                tipo VARCHAR(50),
                id_user INT, -- Referência lógica ao usuário proprietário
                ativo BOOLEAN DEFAULT TRUE
            );
        """, schemaName, schemaName));

        // Tabela MORADOR (Ligada a Unidade)
        jdbcTemplate.execute(String.format("""
            CREATE TABLE %s.morador (
                id_morador SERIAL PRIMARY KEY,
                nome VARCHAR(255) NOT NULL,
                telefone VARCHAR(20),
                id_unidade INT REFERENCES %s.unidade(id_unidade),
                id_user INT, -- Referência lógica ao user global
                ativo BOOLEAN DEFAULT TRUE
            );
        """, schemaName, schemaName));

        // Tabela AREAS
        jdbcTemplate.execute(String.format("""
            CREATE TABLE %s.areas (
                id_area SERIAL PRIMARY KEY,
                nome VARCHAR(255) NOT NULL,
                descricao TEXT,
                capacidade INT,
                ativo BOOLEAN DEFAULT TRUE
            );
        """, schemaName));

        // Tabela RESERVA
        jdbcTemplate.execute(String.format("""
            CREATE TABLE %s.reserva (
                id_reserva SERIAL PRIMARY KEY,
                id_area INT REFERENCES %s.areas(id_area),
                id_user INT NOT NULL,
                data_reserva DATE,
                hora_inicio TIMESTAMP,
                hora_fim TIMESTAMP,
                status VARCHAR(50),
                observacao TEXT
            );
        """, schemaName, schemaName));

        // Tabela OCORRENCIA
        jdbcTemplate.execute(String.format("""
            CREATE TABLE %s.ocorrencia (
                id_ocorrencia SERIAL PRIMARY KEY,
                id_user INT NOT NULL,
                id_unidade INT REFERENCES %s.unidade(id_unidade),
                titulo VARCHAR(255),
                mensagem TEXT,
                categoria VARCHAR(100),
                status VARCHAR(50),
                data_criada DATE DEFAULT CURRENT_DATE,
                id_area INT REFERENCES %s.areas(id_area)
            );
        """, schemaName, schemaName, schemaName));

        // Tabela ENTREGA
        jdbcTemplate.execute(String.format("""
            CREATE TABLE %s.entrega (
                id_entrega SERIAL PRIMARY KEY,
                id_unidade INT REFERENCES %s.unidade(id_unidade),
                id_morador INT REFERENCES %s.morador(id_morador),
                id_user INT, -- Destinatário (se houver user vinculado)
                id_user_registrou INT, -- Porteiro/Zelador
                id_porteiro_registro INT, 
                tipo_entrega VARCHAR(100),
                descricao VARCHAR(255),
                codigo_rastreio VARCHAR(100),
                data_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                status INT
            );
        """, schemaName, schemaName, schemaName));

        // Tabela RETIRADA
        jdbcTemplate.execute(String.format("""
            CREATE TABLE %s.retirada (
                id_retirada SERIAL PRIMARY KEY,
                id_entrega INT REFERENCES %s.entrega(id_entrega),
                id_user_retirou INT,
                data_retirada DATE DEFAULT CURRENT_DATE,
                confirmacao INT,
                observacao TEXT
            );
        """, schemaName, schemaName));
    }
}