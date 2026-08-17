package com.ingeniuri.pos_sistema.repository;

import com.ingeniuri.pos_sistema.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por username.
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Verifica si existe un usuario con ese username.
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con ese email.
     */
    boolean existsByEmail(String email);  // ← ESTE ES EL MÉTODO QUE FALTA

    /**
     * Busca un usuario por email.
     */
    Optional<Usuario> findByEmail(String email);
}