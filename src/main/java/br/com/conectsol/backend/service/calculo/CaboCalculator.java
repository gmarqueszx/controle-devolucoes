package br.com.conectsol.backend.service.calculo;

import java.text.Normalizer;
import org.springframework.stereotype.Component;

@Component
public class CaboCalculator {

    private static final int BASE_SOLO = 60;
    private static final int BASE_PADRAO = 30;
    private static final int BASE_HEPR_POR_INVERSOR = 60;
    private static final int BASE_HEPR_AMPLIACAO = 30;

    public ResultadoCabo calcular(boolean solo, String tipoSistema, int strings, int qtdInversores) {
        int base = solo ? BASE_SOLO : BASE_PADRAO;

        int verm = base * strings;
        int preto = base * strings;

        boolean isAmpliacao = "AMPLIACAO".equals(normalizar(tipoSistema));
        int hepr = isAmpliacao ? BASE_HEPR_AMPLIACAO : base + (BASE_HEPR_POR_INVERSOR * qtdInversores);

        return new ResultadoCabo(verm, preto, hepr);
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        String semAcentos = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcentos.toUpperCase();
    }
}
