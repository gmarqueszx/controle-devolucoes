package br.com.conectsol.backend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "lancamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id", nullable = false)
    private Equipe equipe;

    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento;

    @Column(length = 200)
    private String cliente;

    @Builder.Default
    private Integer sistemas = 1;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    private Boolean retornou;

    @Column(name = "tipo_sistema", length = 50)
    private String tipoSistema;

    private Boolean solo;

    private Integer placas;

    /** Calculado automaticamente pelo StringCalculator a partir de placas e inversores. */
    private Integer strings;

    @OneToMany(mappedBy = "lancamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id")
    @Builder.Default
    private List<LancamentoInversor> inversores = new ArrayList<>();

    /** Calculados automaticamente pelo CaboCalculator a partir de solo, strings e qtd. de inversores. */
    @Column(name = "cabo_solar_verm_enviado")
    private Double caboSolarVermEnviado;

    @Column(name = "cabo_solar_preto_enviado")
    private Double caboSolarPretoEnviado;

    @Column(name = "cabo_hepr_enviado")
    private Double caboHeprEnviado;

    @Column(name = "cabo_solar_verm_devolvido")
    private Double caboSolarVermDevolvido;

    @Column(name = "cabo_solar_preto_devolvido")
    private Double caboSolarPretoDevolvido;

    @Column(name = "cabo_hepr_devolvido")
    private Double caboHeprDevolvido;

    /** Derivados: enviado + ajusteFino - devolvido. */
    @Column(name = "cabo_solar_verm_usado")
    private Double caboSolarVermUsado;

    @Column(name = "cabo_solar_preto_usado")
    private Double caboSolarPretoUsado;

    @Column(name = "cabo_hepr_usado")
    private Double caboHeprUsado;

    @Column(name = "qtd_materiais_enviados")
    private Integer qtdMateriaisEnviados;

    @Column(name = "qtd_materiais_divergentes")
    private Integer qtdMateriaisDivergentes;

    private Double aproveitamento;

    @Column(name = "foto_sobras_grupo")
    private Boolean fotoSobrasGrupo;

    /** Ajustes manuais opcionais (em metros), somados ao cabo enviado calculado de cada cor. */
    @Column(name = "ajuste_fino_verm")
    private Double ajusteFinoVerm;

    @Column(name = "ajuste_fino_preto")
    private Double ajusteFinoPreto;

    @Column(name = "ajuste_fino_hepr")
    private Double ajusteFinoHepr;

    @Column(name = "localizacao_sobra", length = 200)
    private String localizacaoSobra;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "criado_por", length = 100)
    private String criadoPor;
}
