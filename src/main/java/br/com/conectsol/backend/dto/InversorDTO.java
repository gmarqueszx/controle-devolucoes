package br.com.conectsol.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InversorDTO {

    @NotNull(message = "Potencia do inversor e obrigatoria")
    @Positive(message = "Potencia deve ser maior que zero")
    private Double kw;

    @NotNull(message = "Quantidade do inversor e obrigatoria")
    @Positive(message = "Quantidade deve ser maior que zero")
    private Integer quantidade;
}
