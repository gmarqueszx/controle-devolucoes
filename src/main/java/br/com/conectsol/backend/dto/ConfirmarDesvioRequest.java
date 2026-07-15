package br.com.conectsol.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmarDesvioRequest {

    @NotBlank(message = "Justificativa e obrigatoria")
    private String justificativa;

    private String confirmadoPor;
}
