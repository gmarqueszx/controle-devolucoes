package br.com.conectsol.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtService(
            @Value("${jwt.secret}") String segredo,
            @Value("${jwt.expiration}") long expiracaoMs) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes());
        this.expiracaoMs = expiracaoMs;
    }

    public String gerarToken(String email, String perfil) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expiracaoMs);
        return Jwts.builder()
                .subject(email)
                .claim("perfil", perfil)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(chave)
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public String extrairPerfil(String token) {
        return extrairClaim(token, claims -> claims.get("perfil", String.class));
    }

    public boolean tokenValido(String token, String email) {
        String emailDoToken = extrairEmail(token);
        return emailDoToken.equals(email) && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        return extrairClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extrairClaim(String token, Function<Claims, T> resolvedor) {
        Claims claims = Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolvedor.apply(claims);
    }
}
