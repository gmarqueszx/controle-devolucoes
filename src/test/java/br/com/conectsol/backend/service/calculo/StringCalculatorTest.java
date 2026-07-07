package br.com.conectsol.backend.service.calculo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class StringCalculatorTest {

    private final StringCalculator calculator = new StringCalculator();

    @Test
    void deveCalcularUmaStringParaInversorDeBaixaPotencia() {
        int strings = calculator.calcular(10, List.of(new Inversor(8.0, 1)));
        assertThat(strings).isEqualTo(1);
    }

    @Test
    void deveCalcularStringsComMultiplosInversoresDePotenciasDiferentes() {
        int strings = calculator.calcular(20, List.of(new Inversor(8.0, 1), new Inversor(15.0, 1)));
        assertThat(strings).isEqualTo(2);
    }

    @Test
    void deveUsarDivisorAltoParaInversorDe12kwOuMais() {
        // 19 placas / divisor 19 = 1 string; 20 placas ja exigiria 2
        int strings12kw = calculator.calcular(19, List.of(new Inversor(12.0, 1)));
        assertThat(strings12kw).isEqualTo(1);

        int strings20placas = calculator.calcular(20, List.of(new Inversor(12.0, 1)));
        assertThat(strings20placas).isEqualTo(2);
    }

    @Test
    void deveUsarDivisorBaixoParaInversorAbaixoDe12kw() {
        int stringsNoLimite = calculator.calcular(11, List.of(new Inversor(11.9, 1)));
        assertThat(stringsNoLimite).isEqualTo(1);

        int stringsAcimaDoLimite = calculator.calcular(12, List.of(new Inversor(11.9, 1)));
        assertThat(stringsAcimaDoLimite).isEqualTo(2);
    }

    @Test
    void deveRetornarUmQuandoNaoHaPlacas() {
        assertThat(calculator.calcular(0, List.of(new Inversor(8.0, 1)))).isEqualTo(1);
    }

    @Test
    void deveRetornarUmQuandoNaoHaInversores() {
        assertThat(calculator.calcular(10, List.of())).isEqualTo(1);
        assertThat(calculator.calcular(10, null)).isEqualTo(1);
    }
}
