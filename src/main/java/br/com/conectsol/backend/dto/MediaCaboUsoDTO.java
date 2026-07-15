package br.com.conectsol.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaCaboUsoDTO {

    private String tipoSistema;
    private boolean solo;
    private long amostras;
    private Double mediaSolarVermPorString;
    private Double mediaSolarPretoPorString;
    private Double mediaHeprTotal;
}
