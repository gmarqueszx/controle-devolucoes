package br.com.conectsol.backend.service;

import br.com.conectsol.backend.dto.LoginRequest;
import br.com.conectsol.backend.dto.LoginResponse;
import br.com.conectsol.backend.exception.CredenciaisInvalidasException;
import br.com.conectsol.backend.model.Usuario;
import br.com.conectsol.backend.repository.UsuarioRepository;
import br.com.conectsol.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponse autenticar(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .filter(Usuario::getAtivo)
                .orElseThrow(() -> new CredenciaisInvalidasException("Email ou senha invalidos"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException("Email ou senha invalidos");
        }

        String token = jwtService.gerarToken(usuario.getEmail(), usuario.getPerfil().name());

        return LoginResponse.builder()
                .token(token)
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil())
                .build();
    }
}
