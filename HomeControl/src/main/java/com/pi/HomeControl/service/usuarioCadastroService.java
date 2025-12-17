package com.pi.HomeControl.service;

import com.pi.HomeControl.dto.cadastroDto;
import com.pi.HomeControl.model.User;
import com.pi.HomeControl.model.UserRole;
import com.pi.HomeControl.model.condominio;
import com.pi.HomeControl.repository.condominioRepository;
import com.pi.HomeControl.repository.usuarioCadastroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class usuarioCadastroService {

    @Autowired
    private usuarioCadastroRepository userRepository;

    @Autowired
    private condominioRepository condominioRepository;

    @Autowired
    private tenantService tenantService;

    @Transactional
    public User cadastrarUsuarioCompleto(cadastroDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado!");
        }

        User novoUsuario = new User();
        novoUsuario.setNome(dto.getNome());
        novoUsuario.setEmail(dto.getEmail());
        novoUsuario.setTelefone(dto.getTelefone());
        novoUsuario.setSenha(dto.getSenha());
        novoUsuario.setAtivo(true);

        if ("sindico".equalsIgnoreCase(dto.getTipoCadastro())) {
            configurarComoSindico(novoUsuario, dto);
        } else if ("morador".equalsIgnoreCase(dto.getTipoCadastro())) {
            configurarComoMorador(novoUsuario, dto);
        } else {
            throw new RuntimeException("Tipo de cadastro inválido");
        }

        return userRepository.save(novoUsuario);
    }

    private User configurarComoSindico(User user, cadastroDto dto) {
        user.setRole(UserRole.SINDICO);

        String nomeBase = dto.getNomeCondominio().trim().toLowerCase();

        String nomeSemAcento = java.text.Normalizer.normalize(nomeBase, java.text.Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        String schemaName = nomeSemAcento.replaceAll("\\s+", "_")
                .replaceAll("[^a-z0-9_]", "");

        if (condominioRepository.existsBySchemaName(schemaName)) {
            throw new RuntimeException("Já existe um condomínio cadastrado com o nome '" + dto.getNomeCondominio() +
                    "'. Por favor, tente um nome diferente ou contate o suporte.");
        }

        condominio condominio = new condominio();
        condominio.setNome(dto.getNomeCondominio());
        condominio.setSchemaName(schemaName);
        condominio.setCodigoAcesso(schemaName.toUpperCase());
        condominio.setAtivo(true);

        condominio = condominioRepository.save(condominio);

        tenantService.criarSchemaCondominio(schemaName);

        user.setIdCondominio(condominio.getId());

        return userRepository.save(user);
    }

    private void configurarComoMorador(User user, cadastroDto dto) {
        user.setRole(UserRole.MORADOR);
        user.setIdCondominio(2L);
    }
}