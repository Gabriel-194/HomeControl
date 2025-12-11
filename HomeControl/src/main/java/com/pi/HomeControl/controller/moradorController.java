package com.pi.HomeControl.controller;

import com.pi.HomeControl.dto.novoMoradorDto;
import com.pi.HomeControl.service.moradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moradores")
@CrossOrigin(origins = "*")
public class moradorController {

    @Autowired
    private moradorService moradorService;

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarMorador(
            @RequestBody novoMoradorDto dto,
            @RequestParam Long idCondominio) { // Passaremos o ID do condomínio na URL por enquanto
        try {
            moradorService.cadastrarMoradorInterno(dto, idCondominio);
            return ResponseEntity.ok("Morador cadastrado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }
}