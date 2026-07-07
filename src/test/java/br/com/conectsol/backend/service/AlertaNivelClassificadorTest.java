package br.com.conectsol.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.conectsol.backend.model.NivelAlerta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class AlertaNivelClassificadorTest {

    private final AlertaNivelClassificador classificador = new AlertaNivelClassificador();

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Aguardando", "aguardando", "  "})
    void deveClassificarVazioOuAguardandoComoMedio(String statusOriginal) {
        assertThat(classificador.classificar(statusOriginal)).isEqualTo(NivelAlerta.MEDIO);
    }

    @Test
    void deveClassificarJustificadoComoLeve() {
        assertThat(classificador.classificar("Justificado")).isEqualTo(NivelAlerta.LEVE);
    }

    @Test
    void deveClassificarAltoComoAlto() {
        assertThat(classificador.classificar("Alto")).isEqualTo(NivelAlerta.ALTO);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Médio", "Medio", "medio"})
    void deveClassificarMedioComoMedio(String statusOriginal) {
        assertThat(classificador.classificar(statusOriginal)).isEqualTo(NivelAlerta.MEDIO);
    }

    @Test
    void deveClassificarLeveComoLeve() {
        assertThat(classificador.classificar("Leve")).isEqualTo(NivelAlerta.LEVE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Sobra no Galpão", "Galpao", "Sobra", "sobra no galpao"})
    void deveIgnorarSobrasDeGalpao(String statusOriginal) {
        assertThat(classificador.classificar(statusOriginal)).isNull();
    }
}
