package com.ingeniuri.pos_sistema.security;

import com.ingeniuri.pos_sistema.entity.Usuario;
import com.ingeniuri.pos_sistema.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio que adapta nuestro Usuario a Spring Security.
 * Patrón: Adapter (adaptamos nuestra entidad a la interfaz UserDetails)
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscamos al usuario en la BD por su username
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Como Usuario implementa UserDetails, lo devolvemos directamente
        return usuario;
    }
}