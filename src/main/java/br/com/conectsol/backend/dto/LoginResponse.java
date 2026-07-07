package br.com.conectsol.backend.dto;

import br.com.conectsol.backend.model.Perfil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String nome;
    private String email;
    private Perfil perfil;
}
