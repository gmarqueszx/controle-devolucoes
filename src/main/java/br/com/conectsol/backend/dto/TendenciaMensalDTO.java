package br.com.conectsol.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TendenciaMensalDTO {

    private String mes;
    private long alto;
    private long medio;
    private long leve;
    private long total;
}
