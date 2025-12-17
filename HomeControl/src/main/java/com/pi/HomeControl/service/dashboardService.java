package com.pi.HomeControl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pi.HomeControl.dto.dashboardDto;
import com.pi.HomeControl.repository.ocorrenciasRepository;

@Service
public class dashboardService {
    @Autowired
    private  ocorrenciasRepository ocorrenciaRepository;

    public dashboardDto buscarDadosDoDashboard() {
        long pendentes = ocorrenciaRepository.countByStatus("PENDENTE");
        var listaRecente = ocorrenciaRepository.findTop5ByOrderByDataCriacaoDesc();
        return dashboardDto.builder()
                .totalOcorrenciasPendentes(pendentes)
                .atividadesRecentes(listaRecente)
                .build();
    }
}
