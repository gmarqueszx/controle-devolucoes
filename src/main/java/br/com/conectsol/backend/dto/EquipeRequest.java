package br.com.conectsol.backend.dto;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipeRequest {

    @Valid
    @Builder.Default
    private List<EquipeMembroRequest> membros = List.of();
}
