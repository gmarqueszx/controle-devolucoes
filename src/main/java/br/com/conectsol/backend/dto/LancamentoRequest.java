package br.com.conectsol.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LancamentoRequest {

    @NotNull(message = "Equipe e obrigatoria")
    private Long equipeId;

    @NotNull(message = "Data do lancamento e obrigatoria")
    private LocalDate dataLancamento;

    private String cliente;

    @Positive(message = "Sistemas deve ser maior que zero")
    @Builder.Default
    private Integer sistemas = 1;

    private String observacoes;

    private String criadoPor;
}
