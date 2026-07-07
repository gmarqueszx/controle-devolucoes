package br.com.conectsol.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.conectsol.backend.dto.LoginRequest;
import br.com.conectsol.backend.exception.CredenciaisInvalidasException;
import br.com.conectsol.backend.model.Perfil;
import br.com.conectsol.backend.model.Usuario;
import br.com.conectsol.backend.repository.UsuarioRepository;
import br.com.conectsol.backend.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveAutenticarComCredenciaisValidas() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Admin")
                .email("admin@conectsol.com")
                .senhaHash("hash")
                .perfil(Perfil.ADMIN)
                .ativo(true)
                .build();

        when(usuarioRepository.findByEmail("admin@conectsol.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "hash")).thenReturn(true);
        when(jwtService.gerarToken("admin@conectsol.com", "ADMIN")).thenReturn("token-jwt");

        var request = LoginRequest.builder().email("admin@conectsol.com").senha("123456").build();
        var response = authService.autenticar(request);

        assertThat(response.getToken()).isEqualTo("token-jwt");
        assertThat(response.getNome()).isEqualTo("Admin");
        assertThat(response.getPerfil()).isEqualTo(Perfil.ADMIN);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findByEmail("inexistente@conectsol.com")).thenReturn(Optional.empty());

        var request = LoginRequest.builder().email("inexistente@conectsol.com").senha("123456").build();

        assertThatThrownBy(() -> authService.autenticar(request))
                .isInstanceOf(CredenciaisInvalidasException.class);
    }

    @Test
    void deveLancarExcecaoQuandoSenhaInvalida() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("admin@conectsol.com")
                .senhaHash("hash")
                .perfil(Perfil.ADMIN)
                .ativo(true)
                .build();

        when(usuarioRepository.findByEmail("admin@conectsol.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha-errada", "hash")).thenReturn(false);

        var request = LoginRequest.builder().email("admin@conectsol.com").senha("senha-errada").build();

        assertThatThrownBy(() -> authService.autenticar(request))
                .isInstanceOf(CredenciaisInvalidasException.class);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioInativo() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("inativo@conectsol.com")
                .senhaHash("hash")
                .perfil(Perfil.VIEWER)
                .ativo(false)
                .build();

        when(usuarioRepository.findByEmail("inativo@conectsol.com")).thenReturn(Optional.of(usuario));

        var request = LoginRequest.builder().email("inativo@conectsol.com").senha("123456").build();

        assertThatThrownBy(() -> authService.autenticar(request))
                .isInstanceOf(CredenciaisInvalidasException.class);
    }
}
