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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El username ya está en uso");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

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

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEmail(request.getEmail());
        usuario.setRoles(new java.util.ArrayList<>(roles));
        usuario.setEnabled(true);

        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario);
        return AuthResponse.builder()
                .token(token)
                .mensaje("Usuario registrado exitosamente")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        System.out.println("🔐 Intentando login para: " + request.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            System.out.println("✅ Autenticación exitosa");
        } catch (Exception e) {
            System.err.println("❌ Error de autenticación: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generateToken(usuario);
        return AuthResponse.builder()
                .token(token)
                .mensaje("Login exitoso")
                .build();
    }
}