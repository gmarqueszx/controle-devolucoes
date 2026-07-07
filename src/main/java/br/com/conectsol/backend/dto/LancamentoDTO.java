package br.com.conectsol.backend.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LancamentoDTO {

    private Long id;
    private Long equipeId;
    private String montador;
    private String eletricista;
    private LocalDate dataLancamento;
    private String cliente;
    private Integer sistemas;
    private String observacoes;
    private String criadoPor;
}
