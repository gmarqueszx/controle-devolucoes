package br.com.conectsol.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioColaboradorDTO {

    private String nome;
    private long alto;
    private long medio;
    private long leve;
    private long totalAlertas;
    private long pontos;
}
