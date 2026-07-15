package br.com.conectsol.backend.dto;

import java.util.List;
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
    private String ajudante;
    private List<EquipeMembroDTO> membros;
    private Boolean ativa;
}
