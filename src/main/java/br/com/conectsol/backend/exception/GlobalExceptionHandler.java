package br.com.conectsol.backend.exception;

import br.com.conectsol.backend.dto.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex, HttpServletRequest request) {
        return construirResposta(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResponse> tratarCredenciaisInvalidas(
            CredenciaisInvalidasException ex, HttpServletRequest request) {
        return construirResposta(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> tratarAcessoNegado(AccessDeniedException ex, HttpServletRequest request) {
        return construirResposta(HttpStatus.FORBIDDEN, "Sem permissao para esta acao", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return construirResposta(HttpStatus.BAD_REQUEST, mensagem, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroGenerico(Exception ex, HttpServletRequest request) {
        return construirResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno. Tente novamente.", request);
    }

    private ResponseEntity<ErroResponse> construirResposta(
            HttpStatus status, String mensagem, HttpServletRequest request) {
        ErroResponse erro = ErroResponse.builder()
                .status(status.value())
                .erro(mensagem)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(erro);
    }
}
