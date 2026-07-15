package br.com.conectsol.backend.dto;

import br.com.conectsol.backend.model.FuncaoEquipe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipeMembroRequest {

    @NotBlank(message = "Nome do membro e obrigatorio")
    private String nome;

    @NotNull(message = "Funcao do membro e obrigatoria")
    private FuncaoEquipe funcao;
}
