package com.ingeniuri.pos_sistema.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un rol de usuario.
 *
 * Patrones y conceptos:
 * - Entity Pattern (JPA): Mapea esta clase a una tabla
 * - DTO Pattern (implícito): Los roles viajan dentro del Usuario
 *
 * Relación: Muchos-a-Muchos con Usuario
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String nombre;
}