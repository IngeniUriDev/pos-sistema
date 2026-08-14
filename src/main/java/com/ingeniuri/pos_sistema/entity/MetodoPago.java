package com.ingeniuri.pos_sistema.entity;

/**
 * Enumeración de métodos de pago disponibles.
 * Patrón: Enum (valores predefinidos y seguros)
 */
public enum MetodoPago {
    EFECTIVO,
    TARJETA_CREDITO,
    TARJETA_DEBITO,
    TRANSFERENCIA,
    OTRO
}