package br.com.conectsol.backend.dto;

import br.com.conectsol.backend.model.StatusAlerta;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaStatusRequest {

    @NotNull(message = "Status e obrigatorio")
    private StatusAlerta status;
}
