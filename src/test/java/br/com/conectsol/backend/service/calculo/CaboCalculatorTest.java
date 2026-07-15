package br.com.conectsol.backend.service.calculo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CaboCalculatorTest {

    private final CaboCalculator calculator = new CaboCalculator();

    @Test
    void deveCalcularCaboParaTelhadoComumComUmInversor() {
        ResultadoCabo resultado = calculator.calcular(false, "PROJETO", 2, 1);
        assertThat(resultado.verm()).isEqualTo(60);
        assertThat(resultado.preto()).isEqualTo(60);
        assertThat(resultado.hepr()).isEqualTo(90);
    }

    @Test
    void deveCalcularCaboParaSoloComDoisInversores() {
        ResultadoCabo resultado = calculator.calcular(true, "PROJETO", 3, 2);
        assertThat(resultado.verm()).isEqualTo(180);
        assertThat(resultado.preto()).isEqualTo(180);
        assertThat(resultado.hepr()).isEqualTo(180);
    }

    @Test
    void deveUsarBasePadraoQuandoSoloFalso() {
        ResultadoCabo resultado = calculator.calcular(false, "PROJETO", 1, 1);
        assertThat(resultado.verm()).isEqualTo(30);
        assertThat(resultado.hepr()).isEqualTo(90);
    }

    @Test
    void deveUsarHeprFixoEmAmpliacaoIndependenteDeInversores() {
        ResultadoCabo resultado = calculator.calcular(false, "AMPLIACAO", 2, 3);
        assertThat(resultado.hepr()).isEqualTo(30);
        assertThat(resultado.verm()).isEqualTo(60);
        assertThat(resultado.preto()).isEqualTo(60);
    }

    @Test
    void deveUsarHeprFixoEmAmpliacaoNoSolo() {
        ResultadoCabo resultado = calculator.calcular(true, "AMPLIACAO", 1, 4);
        assertThat(resultado.hepr()).isEqualTo(30);
        assertThat(resultado.verm()).isEqualTo(60);
    }
}
