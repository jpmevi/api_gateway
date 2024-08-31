package com.jpmevi.gateway.application.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY = "586E3272357538782F413F4428472B4B6250655368566B597033733676397924";

    // Genera un token JWT para un usuario
    public String getToken(UserDetails user) {
        return generateToken(new HashMap<>(), user);
    }

    // Método interno para generar el token JWT
    private String generateToken(Map<String, Object> extraClaims, UserDetails user) {
        String role = user.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_DEFAULT");

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Se agrega la autoridad (rol) como un claim

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // El token expira en 1 día
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Devuelve la clave secreta utilizada para firmar el JWT
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extrae el nombre de usuario del token JWT
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    // Valida el token JWT para el usuario proporcionado
    public boolean isTokenValid(String token) {
        final String username = getUsernameFromToken(token);
        return (!username.isEmpty() && !isTokenExpired(token));
    }

    public String getRoleFromToken(String token) {
        Claims claims = getAllClaims(token);
        return claims.get("role", String.class);
    }

    // Obtiene todos los claims (reclamos) del token JWT
    private Claims getAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Devuelve un claim específico del token JWT
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Obtiene la fecha de expiración del token JWT
    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    // Verifica si el token JWT ha expirado
    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }
}