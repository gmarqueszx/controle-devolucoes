package br.com.conectsol.backend.dto;

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

    private Boolean retornou;
    private String tipoSistema;
    private String telhado;
    private Integer placas;
    private Integer strings;
    private List<InversorDTO> inversores;

    private Double caboSolarVermEnviado;
    private Double caboSolarPretoEnviado;
    private Double caboHeprEnviado;
    private Double caboSolarVermDevolvido;
    private Double caboSolarPretoDevolvido;
    private Double caboHeprDevolvido;
    private Double caboSolarVermUsado;
    private Double caboSolarPretoUsado;
    private Double caboHeprUsado;

    private Integer qtdMateriaisEnviados;
    private Integer qtdMateriaisDivergentes;
    private Double aproveitamento;
    private Boolean fotoSobrasGrupo;
    private Double ajusteFino;
}
