package br.com.conectsol.backend.service.calculo;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StringCalculator {

    private static final int DIVISOR_POTENCIA_ALTA = 19;
    private static final int DIVISOR_POTENCIA_BAIXA = 11;
    private static final double LIMITE_POTENCIA_KW = 12;

    public int calcular(int placas, List<Inversor> inversores) {
        if (inversores == null || inversores.isEmpty() || placas == 0) {
            return 1;
        }

        double potenciaTotal = inversores.stream().mapToDouble(i -> i.kw() * i.qtd()).sum();
        if (potenciaTotal == 0) {
            return 1;
        }

        int stringsTotal = 0;
        for (Inversor inversor : inversores) {
            int divisor = inversor.kw() >= LIMITE_POTENCIA_KW ? DIVISOR_POTENCIA_ALTA : DIVISOR_POTENCIA_BAIXA;
            double fracao = (inversor.kw() * inversor.qtd()) / potenciaTotal;
            double placasPorUnidade = (placas * fracao) / inversor.qtd();
            int stringsPorUnidade = (int) Math.ceil(placasPorUnidade / divisor);
            stringsTotal += stringsPorUnidade * inversor.qtd();
        }

        return stringsTotal;
    }
}
