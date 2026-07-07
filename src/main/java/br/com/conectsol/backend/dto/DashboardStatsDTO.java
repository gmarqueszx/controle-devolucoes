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

    private long alertasHoje;
    private long alertasSemana;
    private long alertasMes;
    private long equipesAtivas;
    private long sistemasMes;
}
