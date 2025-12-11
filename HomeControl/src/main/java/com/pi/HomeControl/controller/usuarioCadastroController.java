package com.pi.HomeControl.controller;

import com.pi.HomeControl.dto.cadastroDto;
import com.pi.HomeControl.model.User;
import com.pi.HomeControl.service.usuarioCadastroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class usuarioCadastroController {

    @Autowired
    private usuarioCadastroService service;

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody cadastroDto cadastroDTO) {
        try {
            User novoUser = service.cadastrarUsuarioCompleto(cadastroDTO);
            return ResponseEntity.ok(novoUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}