package br.com.conectsol.backend.service;

import br.com.conectsol.backend.model.NivelAlerta;
import java.text.Normalizer;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AlertaNivelClassificador {

    private static final Set<String> IGNORADOS = Set.of("sobra no galpao", "galpao", "sobra");

    public NivelAlerta classificar(String statusOriginal) {
        String normalizado = normalizar(statusOriginal);

        if (normalizado.isEmpty() || normalizado.equals("aguardando")) {
            return NivelAlerta.MEDIO;
        }
        if (normalizado.equals("justificado")) {
            return NivelAlerta.LEVE;
        }
        if (normalizado.equals("alto")) {
            return NivelAlerta.ALTO;
        }
        if (normalizado.equals("medio")) {
            return NivelAlerta.MEDIO;
        }
        if (normalizado.equals("leve")) {
            return NivelAlerta.LEVE;
        }
        if (IGNORADOS.contains(normalizado)) {
            return null;
        }
        return NivelAlerta.MEDIO;
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        String semAcentos = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcentos.toLowerCase();
    }
}
