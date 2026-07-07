package br.com.conectsol.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipeDTO {

    private Long id;
    private String montador;
    private String eletricista;
    private Boolean ativa;
}
