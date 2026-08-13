package com.ingeniuri.pos_sistema.repository;

import com.ingeniuri.pos_sistema.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método personalizado para buscar por username
    Optional<Usuario> findByUsername(String username);

    // Verificar si el username ya existe (para el registro)
    boolean existsByUsername(String username);

    // Verificar si el email ya existe
    boolean existsByEmail(String email);
}