package br.com.conectsol.backend.service.calculo;

import java.text.Normalizer;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class MaterialSistemaCalculator {

    private static final Map<String, Integer> QUANTIDADE_POR_TIPO_SISTEMA = Map.of(
            "PROJETO", 33,
            "AMPLIACAO", 15);

    public Optional<Integer> calcular(String tipoSistema) {
        if (tipoSistema == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(QUANTIDADE_POR_TIPO_SISTEMA.get(normalizar(tipoSistema)));
    }

    private String normalizar(String valor) {
        String semAcentos = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcentos.toUpperCase();
    }
}
