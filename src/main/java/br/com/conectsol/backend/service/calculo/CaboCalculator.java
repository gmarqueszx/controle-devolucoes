package br.com.conectsol.backend.service.calculo;

import org.springframework.stereotype.Component;

@Component
public class CaboCalculator {

    private static final int BASE_SOLO = 60;
    private static final int BASE_PADRAO = 30;
    private static final int BASE_HEPR_POR_INVERSOR = 60;

    public ResultadoCabo calcular(String telhado, int strings, int qtdInversores) {
        boolean isSolo = telhado != null && telhado.toUpperCase().trim().equals("SOLO");
        int base = isSolo ? BASE_SOLO : BASE_PADRAO;

        int verm = base * strings;
        int preto = base * strings;
        int hepr = base + (BASE_HEPR_POR_INVERSOR * qtdInversores);

        return new ResultadoCabo(verm, preto, hepr);
    }
}
