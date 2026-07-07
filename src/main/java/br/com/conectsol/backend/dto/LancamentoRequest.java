package br.com.conectsol.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LancamentoRequest {

    @NotNull(message = "Equipe e obrigatoria")
    private Long equipeId;

    @NotNull(message = "Data do lancamento e obrigatoria")
    private LocalDate dataLancamento;

    private String cliente;

    @Positive(message = "Sistemas deve ser maior que zero")
    @Builder.Default
    private Integer sistemas = 1;

    private String observacoes;

    private String criadoPor;

    private Boolean retornou;
    private String tipoSistema;
    private String telhado;
    private Integer placas;

    @Builder.Default
    private List<InversorDTO> inversores = List.of();

    private Double caboSolarVermDevolvido;
    private Double caboSolarPretoDevolvido;
    private Double caboHeprDevolvido;

    private Integer qtdMateriaisEnviados;
    private Integer qtdMateriaisDivergentes;
    private Boolean fotoSobrasGrupo;

    /** Ajuste manual opcional (em metros), somado ao cabo enviado calculado. */
    private Double ajusteFino;
}
