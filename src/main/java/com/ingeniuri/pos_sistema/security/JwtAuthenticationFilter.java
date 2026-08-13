package com.ingeniuri.pos_sistema.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta cada petición HTTP para validar el token JWT.
 * Patrón: Interceptor / Chain of Responsibility
 * Extiende OncePerRequestFilter para garantizar que se ejecute solo una vez por petición.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extraer el header "Authorization" de la petición HTTP
        final String authHeader = request.getHeader("Authorization");

        // 2. Validar que el header exista y empiece con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Si no hay token, dejamos que la petición continúe
            return;
        }

        // 3. Extraer el token (quitando la palabra "Bearer ")
        final String jwt = authHeader.substring(7);

        // 4. Extraer el username del token
        final String username = jwtService.extractUsername(jwt);

        // 5. Si hay un username y aún no está autenticado en el contexto de Spring
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 6. Validar que el token sea válido para este usuario
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 7. Crear el objeto de autenticación y guardarlo en el contexto de seguridad
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Las credenciales (password) son null porque ya validamos con el token
                        userDetails.getAuthorities() // Los roles/permisos del usuario
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Continuar con la cadena de filtros (hacia el Controlador)
        filterChain.doFilter(request, response);
    }
}
