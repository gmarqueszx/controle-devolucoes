package br.com.conectsol.backend.dto;

import br.com.conectsol.backend.model.NivelAlerta;
import br.com.conectsol.backend.model.StatusAlerta;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaDTO {

    private Long id;
    private Long equipeId;
    private String montador;
    private String eletricista;
    private String cliente;
    private LocalDate dataAlerta;
    private String descricao;
    private NivelAlerta nivel;
    private StatusAlerta status;
    private String statusOriginal;
    private String origem;
    private LocalDateTime confirmadoDesvioEm;
    private String confirmadoDesvioPor;
    private String justificativaConfirmacao;
}
