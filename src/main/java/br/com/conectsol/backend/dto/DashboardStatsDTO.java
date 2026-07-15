package br.com.conectsol.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {

    private long sistemasPeriodo;
    private Double percentualAproveitamento100;
    private long sobrasPendentes;
    private long alertasAltoPeriodo;
}
