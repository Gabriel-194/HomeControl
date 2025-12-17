package com.pi.HomeControl.controller;

import com.pi.HomeControl.dto.dashboardDto;
import com.pi.HomeControl.service.dashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // Cuidado: Não use @RestController aqui
import org.springframework.ui.Model; // Importante: org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class dashboardController {
    @Autowired
    private dashboardService dashboardService;

    @GetMapping({"/dashboard", "/"})
    public String carregarHome(Model model) {

        try {
            // 1. Chama o Service para pegar o objeto DTO completo (com counts e listas)
            dashboardDto dadosDoDashboard = dashboardService.buscarDadosDoDashboard();

            // 2. Coloca esse objeto dentro do Model para o Thymeleaf conseguir ler
            // O nome "dashboard" é a chave que você vai usar no HTML (ex: ${dashboard.totalOcorrencias})
            model.addAttribute("dashboard", dadosDoDashboard);

        } catch (Exception e) {
            // Dica: Se der erro no banco, evita que a página quebre inteira
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao carregar dados do dashboard.");
        }

        // 3. Retorna o nome exato do arquivo HTML que está na pasta 'templates' (sem o .html)
        return "dashboard";
    }
}
