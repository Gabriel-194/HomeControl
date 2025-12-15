package com.pi.HomeControl.controller;

import com.pi.HomeControl.dto.moradoresResponseDto;
import com.pi.HomeControl.dto.novoMoradorDto;
import com.pi.HomeControl.dto.usuarioSessao;
import com.pi.HomeControl.service.moradorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moradores")
@CrossOrigin(origins = "*")
public class moradorController {

    @Autowired
    private moradorService moradorService;

    // Listar
    @GetMapping
    public ResponseEntity<?> listar(HttpSession session) {
        usuarioSessao user = (usuarioSessao) session.getAttribute("usuarioLogado");
        if (user == null) return ResponseEntity.status(401).body("Não autenticado");
        return ResponseEntity.ok(moradorService.listarMoradores(user.getIdCondominio()));
    }

    // Cadastrar
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody novoMoradorDto dto, HttpSession session) {
        usuarioSessao user = (usuarioSessao) session.getAttribute("usuarioLogado");
        if (user == null) return ResponseEntity.status(401).body("Não autenticado");

        try {
            moradorService.cadastrarMoradorInterno(dto, user.getIdCondominio());
            return ResponseEntity.ok("Morador cadastrado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    // Editar (Importante para suas edições funcionarem)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody novoMoradorDto dto, HttpSession session) {
        usuarioSessao user = (usuarioSessao) session.getAttribute("usuarioLogado");
        if (user == null) return ResponseEntity.status(401).body("Não autenticado");

        try {
            moradorService.atualizarMorador(id, dto, user.getIdCondominio());
            return ResponseEntity.ok("Morador atualizado!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    // Alterar Status (Importante para o botão Desativar funcionar)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(@PathVariable Long id, @RequestParam boolean ativo, HttpSession session) {
        usuarioSessao user = (usuarioSessao) session.getAttribute("usuarioLogado");
        if (user == null) return ResponseEntity.status(401).body("Não autenticado");

        try {
            moradorService.alternarStatus(user.getIdCondominio(), id, ativo);
            return ResponseEntity.ok("Status alterado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }
}