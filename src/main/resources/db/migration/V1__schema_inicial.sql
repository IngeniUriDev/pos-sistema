-- V1__schema_inicial.sql
-- Script inicial: crea las tablas base del sistema POS

-- Tabla de roles (ADMIN, VENDEDOR, CAJERO)
CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       nombre VARCHAR(20) NOT NULL UNIQUE
);

-- Tabla de usuarios
CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(50) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          nombre_completo VARCHAR(100) NOT NULL,
                          email VARCHAR(100) UNIQUE,
                          enabled BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla intermedia: usuarios_roles (relación muchos-a-muchos)
CREATE TABLE usuarios_roles (
                                usuario_id BIGINT NOT NULL,
                                rol_id BIGINT NOT NULL,
                                PRIMARY KEY (usuario_id, rol_id),
                                FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
                                FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Tabla de categorías de productos
CREATE TABLE categorias (
                            id BIGSERIAL PRIMARY KEY,
                            nombre VARCHAR(50) NOT NULL UNIQUE,
                            descripcion VARCHAR(255),
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de productos
CREATE TABLE productos (
                           id BIGSERIAL PRIMARY KEY,
                           nombre VARCHAR(100) NOT NULL,
                           descripcion TEXT,
                           precio NUMERIC(10, 2) NOT NULL CHECK (precio >= 0),
                           stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
                           categoria_id BIGINT NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- Índices para mejorar rendimiento de consultas frecuentes
CREATE INDEX idx_productos_categoria ON productos(categoria_id);
CREATE INDEX idx_productos_nombre ON productos(nombre);
CREATE INDEX idx_usuarios_username ON usuarios(username);

-- Datos iniciales: roles del sistema
INSERT INTO roles (nombre) VALUES
                               ('ROLE_ADMIN'),
                               ('ROLE_VENDEDOR'),
                               ('ROLE_CAJERO');

-- Usuario admin por defecto (password: admin123 en BCrypt)
INSERT INTO usuarios (username, password, nombre_completo, email, enabled)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Administrador', 'admin@pos.com', TRUE);

-- Asignar rol ADMIN al usuario admin
INSERT INTO usuarios_roles (usuario_id, rol_id)
VALUES (1, 1);