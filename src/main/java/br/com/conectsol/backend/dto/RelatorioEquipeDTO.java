package br.com.conectsol.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioEquipeDTO {

    private String montador;
    private String eletricista;
    private long alto;
    private long medio;
    private long leve;
    private long totalAlertas;
    private long sistemas;
    private double indiceAlertasPorSistema;
}
