package com.pi.HomeControl.service;


import com.pi.HomeControl.dto.moradoresResponseDto;
import com.pi.HomeControl.dto.novoMoradorDto;
import com.pi.HomeControl.repository.condominioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class moradorService {

    @Autowired
    private condominioRepository condominioRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // --- LISTAR ---
    public List<moradoresResponseDto> listarMoradores(Long idCondominio) {
        String schema = getSchemaById(idCondominio);
        String sql = String.format("""
            SELECT 
                m.id_morador as id, 
                m.nome, 
                m.telefone, 
                b.nome as bloco, 
                u.numero as unidade, 
                m.ativo, 
                'Morador' as perfil,
                (SELECT usr.email FROM public.users usr WHERE usr.id_user = m.id_user) as email
            FROM %s.morador m
            JOIN %s.unidade u ON m.id_unidade = u.id_unidade
            JOIN %s.bloco b ON u.id_bloco = b.id_bloco
            ORDER BY m.nome ASC
        """, schema, schema, schema);

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(moradoresResponseDto.class));
    }

    // --- CADASTRAR ---
    @Transactional
    public void cadastrarMoradorInterno(novoMoradorDto dto, Long idCondominio) {
        String schema = getSchemaById(idCondominio);

        // Para as tabelas locais (do schema), precisamos dos IDs (PK)
        Integer idBloco = buscarOuCriarBloco(schema, dto.getNomeBloco());
        Integer idUnidadePK = buscarOuCriarUnidade(schema, idBloco, dto.getNumeroUnidade());

        // 1. Cria usuário Global passando o NÚMERO REAL (ex: 35) e não o ID
        Integer idUserGlobal = criarOuBuscarUsuarioGlobal(
                dto.getNome(),
                dto.getEmail(),
                dto.getTelefone(),
                idCondominio,
                dto.getNumeroUnidade() // <--- MUDANÇA AQUI: Passando o 35
        );

        // 2. Cria Morador Local (Usa a PK para relação correta no banco local)
        String sqlMorador = String.format("INSERT INTO %s.morador (nome, telefone, id_unidade, id_user, ativo) VALUES (?, ?, ?, ?, true)", schema);
        jdbcTemplate.update(sqlMorador, dto.getNome(), dto.getTelefone(), idUnidadePK, idUserGlobal);

        // 3. Atualiza unidade com o dono
        String sqlUpdateUnidade = String.format("UPDATE %s.unidade SET id_user = ? WHERE id_unidade = ?", schema);
        jdbcTemplate.update(sqlUpdateUnidade, idUserGlobal, idUnidadePK);
    }

    // --- ATUALIZAR ---
    @Transactional
    public void atualizarMorador(Long idMorador, novoMoradorDto dto, Long idCondominio) {
        String schema = getSchemaById(idCondominio);

        // Busca/Cria IDs para o schema local
        Integer idBloco = buscarOuCriarBloco(schema, dto.getNomeBloco());
        Integer idUnidadePK = buscarOuCriarUnidade(schema, idBloco, dto.getNumeroUnidade());

        // 2. Atualiza tabela LOCAL (morador) usando a PK correta
        String sqlUpdateLocal = String.format("UPDATE %s.morador SET nome = ?, telefone = ?, id_unidade = ? WHERE id_morador = ?", schema);
        jdbcTemplate.update(sqlUpdateLocal, dto.getNome(), dto.getTelefone(), idUnidadePK, idMorador);

        // 3. Recupera o ID Global
        Integer idUserGlobal = getIdUserGlobal(schema, idMorador);

        // 4. Atualiza tabela GLOBAL (users)
        if (idUserGlobal != null) {
            // AQUI ESTÁ A CORREÇÃO: Usamos dto.getNumeroUnidade() (ex: 35) no lugar do ID
            String sqlUpdateGlobal = "UPDATE public.users SET nome = ?, email = ?, telefone = ?, id_unidade = ? WHERE id_user = ?";
            jdbcTemplate.update(sqlUpdateGlobal,
                    dto.getNome(),
                    dto.getEmail(),
                    dto.getTelefone(),
                    dto.getNumeroUnidade(), // <--- SALVA O NÚMERO 35
                    idUserGlobal
            );
        } else {
            vincularUsuarioExistente(schema, idMorador, dto.getEmail());
        }
    }

    // --- ALTERAR STATUS ---
    @Transactional
    public void alternarStatus(Long idCondominio, Long idMorador, boolean ativo) {
        String schema = getSchemaById(idCondominio);

        // 1. Atualiza LOCAL
        String sqlLocal = String.format("UPDATE %s.morador SET ativo = ? WHERE id_morador = ?", schema);
        jdbcTemplate.update(sqlLocal, ativo, idMorador);

        // 2. Atualiza GLOBAL
        Integer idUserGlobal = getIdUserGlobal(schema, idMorador);

        if (idUserGlobal != null) {
            String sqlGlobal = "UPDATE public.users SET ativo = ? WHERE id_user = ?";
            jdbcTemplate.update(sqlGlobal, ativo, idUserGlobal);
        }
    }

    // --- MÉTODOS AUXILIARES ---

    private Integer getIdUserGlobal(String schema, Long idMorador) {
        try {
            String sql = String.format("SELECT id_user FROM %s.morador WHERE id_morador = ?", schema);
            return jdbcTemplate.queryForObject(sql, Integer.class, idMorador);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Integer criarOuBuscarUsuarioGlobal(String nome, String email, String telefone, Long idCondominio, Integer numeroUnidade) {
        try {
            // Agora salva o numeroUnidade (ex: 35) na coluna id_unidade
            String sqlUser = "INSERT INTO public.users (nome, email, telefone, senha, role, ativo, id_condominio, id_unidade) VALUES (?, ?, ?, ?, 'MORADOR', true, ?, ?) RETURNING id_user";
            return jdbcTemplate.queryForObject(sqlUser, Integer.class, nome, email, telefone, telefone, idCondominio, numeroUnidade);
        } catch (Exception e) {
            // Se falhar (ex: email duplicado), busca o existente
            String sqlBusca = "SELECT id_user FROM public.users WHERE email = ?";
            try {
                return jdbcTemplate.queryForObject(sqlBusca, Integer.class, email);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private void vincularUsuarioExistente(String schema, Long idMorador, String email) {
        try {
            String sqlBusca = "SELECT id_user FROM public.users WHERE email = ?";
            Integer idUser = jdbcTemplate.queryForObject(sqlBusca, Integer.class, email);

            if (idUser != null) {
                String sqlLink = String.format("UPDATE %s.morador SET id_user = ? WHERE id_morador = ?", schema);
                jdbcTemplate.update(sqlLink, idUser, idMorador);
            }
        } catch (Exception e) { /* Ignora */ }
    }

    private String getSchemaById(Long idCondominio) {
        return condominioRepository.findById(idCondominio)
                .orElseThrow(() -> new RuntimeException("Condomínio não encontrado"))
                .getSchemaName();
    }

    private Integer buscarOuCriarBloco(String schema, String nomeBloco) {
        String sqlBusca = String.format("SELECT id_bloco FROM %s.bloco WHERE nome = ?", schema);
        try {
            return jdbcTemplate.queryForObject(sqlBusca, Integer.class, nomeBloco);
        } catch (EmptyResultDataAccessException e) {
            String sqlInsert = String.format("INSERT INTO %s.bloco (nome, ativo) VALUES (?, true) RETURNING id_bloco", schema);
            return jdbcTemplate.queryForObject(sqlInsert, Integer.class, nomeBloco);
        }
    }

    private Integer buscarOuCriarUnidade(String schema, Integer idBloco, Integer numero) {
        String sqlBusca = String.format("SELECT id_unidade FROM %s.unidade WHERE id_bloco = ? AND numero = ?", schema);
        try {
            return jdbcTemplate.queryForObject(sqlBusca, Integer.class, idBloco, numero);
        } catch (EmptyResultDataAccessException e) {
            String sqlInsert = String.format("INSERT INTO %s.unidade (id_bloco, numero, tipo, ativo) VALUES (?, ?, 'Apartamento', true) RETURNING id_unidade", schema, idBloco, numero);
            return jdbcTemplate.queryForObject(sqlInsert, Integer.class, idBloco, numero);
        }
    }
}