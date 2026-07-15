package br.com.conectsol.backend.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Montador e obrigatorio")
    private String montador;

    private String eletricista;
    private String ajudante;

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
    private Boolean solo;
    private Integer placas;

    @Builder.Default
    private List<InversorDTO> inversores = List.of();

    private Double caboSolarVermDevolvido;
    private Double caboSolarPretoDevolvido;
    private Double caboHeprDevolvido;

    private Integer qtdMateriaisEnviados;
    private Integer qtdMateriaisDivergentes;
    private Boolean fotoSobrasGrupo;

    /** Ajustes manuais opcionais (em metros), somados ao cabo enviado calculado de cada cor. */
    private Double ajusteFinoVerm;
    private Double ajusteFinoPreto;
    private Double ajusteFinoHepr;

    private String localizacaoSobra;
}
