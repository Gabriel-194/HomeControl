package com.pi.HomeControl.service;

import com.pi.HomeControl.dto.novoMoradorDto;
import com.pi.HomeControl.model.condominio;
import com.pi.HomeControl.repository.condominioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class moradorService {

    @Autowired
    private condominioRepository condominioRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void cadastrarMoradorInterno(novoMoradorDto dto, Long idCondominio) {
        condominio condominio = condominioRepository.findById(idCondominio)
                .orElseThrow(() -> new RuntimeException("Condomínio não encontrado"));

        String schema = condominio.getSchemaName();

        Integer idBloco = buscarOuCriarBloco(schema, dto.getNomeBloco());

        Integer idUnidade = buscarOuCriarUnidade(schema, idBloco, dto.getNumeroUnidade());

        String sqlMorador = String.format("INSERT INTO %s.morador (nome, telefone, id_unidade, ativo) VALUES (?, ?, ?, true)", schema);
        jdbcTemplate.update(sqlMorador, dto.getNome(), dto.getTelefone(), idUnidade);

    }

    private Integer buscarOuCriarBloco(String schema, String nomeBloco) {
        String sqlBusca = String.format("SELECT id_bloco FROM %s.bloco WHERE nome = ?", schema);
        try {
            return jdbcTemplate.queryForObject(sqlBusca, Integer.class, nomeBloco);
        } catch (Exception e) {
            // Não existe, cria
            String sqlInsert = String.format("INSERT INTO %s.bloco (nome, ativo) VALUES (?, true) RETURNING id_bloco", schema);
            return jdbcTemplate.queryForObject(sqlInsert, Integer.class, nomeBloco);
        }
    }

    private Integer buscarOuCriarUnidade(String schema, Integer idBloco, Integer numero) {
        String sqlBusca = String.format("SELECT id_unidade FROM %s.unidade WHERE id_bloco = ? AND numero = ?", schema);
        try {
            return jdbcTemplate.queryForObject(sqlBusca, Integer.class, idBloco, numero);
        } catch (Exception e) {
            // Não existe, cria
            String sqlInsert = String.format("INSERT INTO %s.unidade (id_bloco, numero, tipo, ativo) VALUES (?, ?, 'Apartamento', true) RETURNING id_unidade", schema, idBloco, numero);
            return jdbcTemplate.queryForObject(sqlInsert, Integer.class, idBloco, numero);
        }
    }
}