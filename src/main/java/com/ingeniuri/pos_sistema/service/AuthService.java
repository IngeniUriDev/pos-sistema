package com.ingeniuri.pos_sistema.service;

import com.ingeniuri.pos_sistema.dto.AuthResponse;
import com.ingeniuri.pos_sistema.dto.LoginRequest;
import com.ingeniuri.pos_sistema.dto.RegisterRequest;
import com.ingeniuri.pos_sistema.entity.Rol;
import com.ingeniuri.pos_sistema.entity.Usuario;
import com.ingeniuri.pos_sistema.repository.RolRepository;
import com.ingeniuri.pos_sistema.repository.UsuarioRepository;
import com.ingeniuri.pos_sistema.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Servicio que maneja la lógica de autenticación y registro.
 * Patrón: Service Layer / Facade
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra un nuevo usuario en el sistema.
     */
    public AuthResponse register(RegisterRequest request) {
        // Verificar si el username o email ya existen
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El username ya está en uso");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

        // Determinar los roles del usuario (por defecto ROLE_VENDEDOR si no se especifican)
        Set<Rol> roles = new HashSet<>();
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            Rol rolVendedor = rolRepository.findByNombre("ROLE_VENDEDOR")
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_VENDEDOR no encontrado"));
            roles.add(rolVendedor);
        } else {
            for (String rolNombre : request.getRoles()) {
                Rol rol = rolRepository.findByNombre(rolNombre)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolNombre));
                roles.add(rol);
            }
        }

        // Crear el usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar contraseña
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEmail(request.getEmail());
        usuario.setRoles(new java.util.ArrayList<>(roles));
        usuario.setEnabled(true);

        usuarioRepository.save(usuario);

        // Generar token
        String token = jwtService.generateToken(usuario);
        return AuthResponse.builder()
                .token(token)
                .mensaje("Usuario registrado exitosamente")
                .build();
    }

    /**
     * Autentica un usuario y devuelve un token JWT.
     */
    public AuthResponse login(LoginRequest request) {
        // Spring Security valida las credenciales
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Si no lanza excepción, las credenciales son correctas
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar token
        String token = jwtService.generateToken(usuario);
        return AuthResponse.builder()
                .token(token)
                .mensaje("Login exitoso")
                .build();
    }
}