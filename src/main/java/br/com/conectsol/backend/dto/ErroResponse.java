package br.com.conectsol.backend.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErroResponse {

    private int status;
    private String erro;
    private String path;
    private LocalDateTime timestamp;
}
