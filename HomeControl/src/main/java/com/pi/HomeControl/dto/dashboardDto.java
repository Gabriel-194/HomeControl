package com.pi.HomeControl.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;
import com.pi.HomeControl.model.ocorrencias;

@Data
@Builder
public class dashboardDto {
    private long totalOcorrenciasPendentes;
    private long totalReservasHoje;
    private long totalMoradores;
    private List<ocorrencias> atividadesRecentes;
}
