// Tipos compartidos en toda la aplicación
// ¿Por qué TypeScript? Detecta errores en tiempo de compilación,
// no en producción. Es el estándar en empresas serias.

export interface Usuario {
    id: number;
    username: string;
    nombreCompleto: string;
    email: string;
    roles: string[];
}

export interface AuthResponse {
    token: string;
    mensaje: string;
}

export interface LoginRequest {
    username: string;
    password: string;
}

export interface Categoria {
    id: number;
    nombre: string;
    descripcion?: string;
}

export interface Producto {
    id: number;
    nombre: string;
    descripcion?: string;
    precio: number;
    stock: number;
    categoriaId: number;
    categoriaNombre?: string;
}

export interface DetalleVenta {
    productoId: number;
    cantidad: number;
}

export interface VentaRequest {
    metodoPago: 'EFECTIVO' | 'TARJETA_CREDITO' | 'TARJETA_DEBITO' | 'TRANSFERENCIA';
    productos: DetalleVenta[];
    clienteId?: number;
}

export interface Venta {
    id: number;
    fechaVenta: string;
    vendedorNombre: string;
    subtotal: number;
    impuesto: number;
    total: number;
    metodoPago: string;
    detalles: DetalleVenta[];
}