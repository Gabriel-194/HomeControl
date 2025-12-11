package com.pi.HomeControl.controller;

import com.pi.HomeControl.dto.loginDto;
import com.pi.HomeControl.dto.usuarioSessao;
import com.pi.HomeControl.model.condominio;
import com.pi.HomeControl.model.User;
import com.pi.HomeControl.repository.condominioRepository;
import com.pi.HomeControl.repository.usuarioCadastroRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class authController {

    @Autowired
    private usuarioCadastroRepository userRepository;

    @Autowired
    private condominioRepository condominioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody loginDto login, HttpSession session) {

        User user = userRepository.findByEmail(login.getEmail());

        if (user == null || !user.getSenha().equals(login.getSenha())) {
            return ResponseEntity.status(401).body("Email ou senha inválidos.");
        }

        if (!user.getAtivo()) {
            return ResponseEntity.status(403).body("Usuário inativo.");
        }

        String schemaName = "public";
        if (user.getIdCondominio() != null) {
            condominio cond = condominioRepository.findById(user.getIdCondominio()).orElse(null);
            if (cond != null) {
                schemaName = cond.getSchemaName();
            }
        }

        // 4. Criar objeto de sessão
        usuarioSessao usuarioLogado = new usuarioSessao(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().toString() : "USER",
                user.getIdCondominio(),
                schemaName
        );

        // 5. Salvar na Sessão HTTP
        session.setAttribute("usuarioLogado", usuarioLogado);
        // Configura tempo de expiração (ex: 30 minutos em segundos)
        session.setMaxInactiveInterval(30 * 60);

        return ResponseEntity.ok(usuarioLogado);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // Destrói a sessão
        return ResponseEntity.ok("Logout realizado.");
    }

    // Endpoint para o front verificar quem está logado ao carregar a página
    @GetMapping("/me")
    public ResponseEntity<?> getUsuarioLogado(HttpSession session) {
        usuarioSessao usuario = (usuarioSessao) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return ResponseEntity.status(401).body("Não autenticado");
        }
        return ResponseEntity.ok(usuario);
    }
}