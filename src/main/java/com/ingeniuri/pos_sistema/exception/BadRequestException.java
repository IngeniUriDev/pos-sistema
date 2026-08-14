package com.ingeniuri.pos_sistema.exception;

/**
 * Excepción para errores de validación o datos incorrectos.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}