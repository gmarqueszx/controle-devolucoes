package br.com.conectsol.backend.dto;

import br.com.conectsol.backend.model.FuncaoEquipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipeMembroDTO {

    private String nome;
    private FuncaoEquipe funcao;
}
