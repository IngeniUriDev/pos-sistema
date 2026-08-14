package com.ingeniuri.pos_sistema.exception;

/**
 * Excepción personalizada para cuando no se encuentra un recurso.
 * Patrón: Custom Exception
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}