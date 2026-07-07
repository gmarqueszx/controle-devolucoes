package br.com.conectsol.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaRequest {

    @NotNull(message = "Equipe e obrigatoria")
    private Long equipeId;

    private Long lancamentoId;

    @NotNull(message = "Data do alerta e obrigatoria")
    private LocalDate dataAlerta;

    private String descricao;

    private String statusOriginal;
}
