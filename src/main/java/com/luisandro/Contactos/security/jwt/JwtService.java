package com.luisandro.Contactos.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationAndValidation2025}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")  // 24 horas em milissegundos
    private int jwtExpiration;

    // Gera chave de assinatura a partir do secret
    private Key getKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Gera token a partir do usuário autenticado
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())  // Usuário (email ou username)
                .setIssuedAt(now)                       // Data de criação
                .setExpiration(expiryDate)              // Data de expiração
                .signWith(getKey(), SignatureAlgorithm.HS256)  // Assinatura
                .compact();
    }

    // Extrai username do token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Valida se o token é válido
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Token JWT mal formatado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT não suportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Token JWT vazio: {}", e.getMessage());
        }
        return false;
    }
}