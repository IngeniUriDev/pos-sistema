-- V2__tablas_ventas.sql
-- Migración 2: Crea las tablas para el módulo de ventas

-- Tabla de clientes
CREATE TABLE IF NOT EXISTS clientes (
                                        id BIGSERIAL PRIMARY KEY,
                                        nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    telefono VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Tabla de ventas
CREATE TABLE IF NOT EXISTS ventas (
                                      id BIGSERIAL PRIMARY KEY,
                                      fecha_venta TIMESTAMP NOT NULL,
                                      cliente_id BIGINT,
                                      usuario_id BIGINT NOT NULL,
                                      subtotal NUMERIC(10, 2) NOT NULL,
    impuesto NUMERIC(10, 2) NOT NULL,
    total NUMERIC(10, 2) NOT NULL,
    metodo_pago VARCHAR(20) NOT NULL,
    referencia_pago VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE SET NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
    );

-- Tabla de detalles de venta (productos en cada venta)
CREATE TABLE IF NOT EXISTS detalles_venta (
                                              id BIGSERIAL PRIMARY KEY,
                                              venta_id BIGINT NOT NULL,
                                              producto_id BIGINT NOT NULL,
                                              cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10, 2) NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id)
    );

-- Índices para mejorar rendimiento
CREATE INDEX idx_ventas_fecha ON ventas(fecha_venta);
CREATE INDEX idx_ventas_usuario ON ventas(usuario_id);
CREATE INDEX idx_detalles_venta ON detalles_venta(venta_id);