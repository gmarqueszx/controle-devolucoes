package br.com.conectsol.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email e obrigatorio")
    private String email;

    @NotBlank(message = "Senha e obrigatoria")
    private String senha;
}
