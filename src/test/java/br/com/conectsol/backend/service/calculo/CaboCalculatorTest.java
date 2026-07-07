package br.com.conectsol.backend.service.calculo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CaboCalculatorTest {

    private final CaboCalculator calculator = new CaboCalculator();

    @Test
    void deveCalcularCaboParaTelhadoPadraoComUmInversor() {
        ResultadoCabo resultado = calculator.calcular("ZINCO", 2, 1);
        assertThat(resultado.verm()).isEqualTo(60);
        assertThat(resultado.preto()).isEqualTo(60);
        assertThat(resultado.hepr()).isEqualTo(90);
    }

    @Test
    void deveCalcularCaboParaTelhadoSoloComDoisInversores() {
        ResultadoCabo resultado = calculator.calcular("SOLO", 3, 2);
        assertThat(resultado.verm()).isEqualTo(180);
        assertThat(resultado.preto()).isEqualTo(180);
        assertThat(resultado.hepr()).isEqualTo(180);
    }

    @Test
    void deveTratarSoloIndependenteDeCaixaOuEspacos() {
        ResultadoCabo resultado = calculator.calcular("  solo  ", 1, 1);
        assertThat(resultado.verm()).isEqualTo(60);
    }

    @Test
    void deveUsarBasePadraoQuandoTelhadoNulo() {
        ResultadoCabo resultado = calculator.calcular(null, 1, 1);
        assertThat(resultado.verm()).isEqualTo(30);
        assertThat(resultado.hepr()).isEqualTo(90);
    }
}
