package com.jpmevi.gateway.infrastructure.security;

import com.jpmevi.gateway.application.enums.Role;
import com.jpmevi.gateway.application.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final JwtService jwtService;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            try{
                String token = extractToken(exchange.getRequest());

                if (token == null) {
                    // Lanzar excepción si el token no está presente
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token JWT no proporcionado"));
                }

                // Validar la integridad del token
                if (!jwtService.isTokenValid(token)) {
                    // Lanzar excepción si el token es inválido o ha expirado
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token JWT inválido o expirado"));
                }

                // Obtener el rol del token
                String role = jwtService.getRoleFromToken(token);

                // Configurar una autenticación básica basada en el token
                AbstractAuthenticationToken auth = new AbstractAuthenticationToken(List.of(new SimpleGrantedAuthority(role))) {
                    @Override
                    public Object getCredentials() {
                        return token;
                    }

                    @Override
                    public Object getPrincipal() {
                        return jwtService.getUsernameFromToken(token);
                    }
                };

                auth.setAuthenticated(true);  // Configura el token como autenticado

                // Continuar con la cadena de filtros con el contexto de autenticación configurado
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
            }catch (Exception e){
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token JWT inválido o expirado"));
            }
        };
    }

    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
