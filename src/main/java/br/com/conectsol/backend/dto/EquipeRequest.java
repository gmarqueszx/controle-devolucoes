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
public class EquipeRequest {

    @NotBlank(message = "Montador e obrigatorio")
    private String montador;

    private String eletricista;
}
