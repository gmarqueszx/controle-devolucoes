package br.com.conectsol.backend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "equipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EquipeMembro> membros = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativa = true;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    /** Derivado dos membros com funcao MONTADOR; mantido para nao quebrar leituras existentes. */
    public String getMontador() {
        return nomesPorFuncao(FuncaoEquipe.MONTADOR);
    }

    /** Derivado dos membros com funcao ELETRICISTA; mantido para nao quebrar leituras existentes. */
    public String getEletricista() {
        return nomesPorFuncao(FuncaoEquipe.ELETRICISTA);
    }

    public String getAjudante() {
        return nomesPorFuncao(FuncaoEquipe.AJUDANTE);
    }

    private String nomesPorFuncao(FuncaoEquipe funcao) {
        if (membros == null) {
            return "";
        }
        return membros.stream()
                .filter(m -> m.getFuncao() == funcao)
                .map(EquipeMembro::getNome)
                .collect(Collectors.joining(", "));
    }
}
