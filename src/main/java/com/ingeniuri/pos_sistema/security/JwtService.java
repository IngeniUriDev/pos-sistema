package com.ingeniuri.pos_sistema.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para generar y validar tokens JWT.
 * Patrón: Service (encapsula la lógica de negocio de JWT)
 */
@Service
public class JwtService {

    // Clave secreta para firmar el token (en producción, esto va en application.properties o un vault)
    // Debe tener al menos 256 bits (32 caracteres) para el algoritmo HS256
    private static final String SECRET_KEY = "MiClaveSecretaSuperSeguraDeAlMenos32Caracteres1234";

    /**
     * Genera un token JWT para el usuario dado.
     * Incluye los roles del usuario en el payload.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Agregamos los roles al token (para evitar consultar la BD en cada petición)
        claims.put("roles", userDetails.getAuthorities());

        return generateToken(claims, userDetails);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername()) // El "dueño" del token
                .issuedAt(new Date(System.currentTimeMillis())) // Fecha de creación
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Expira en 10 horas
                .signWith(getSignInKey(), Jwts.SIG.HS256) // Firmamos con nuestra clave secreta
                .compact();
    }

    /**
     * Valida si el token es correcto y pertenece al usuario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Extrae el username del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Métodos auxiliares para extraer datos del token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey()) // Verificamos la firma con la misma clave
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Convierte nuestra cadena de texto en una clave criptográfica válida
    private SecretKey getSignInKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}