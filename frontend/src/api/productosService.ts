import api from './axios';
import type { Producto, Categoria } from '../types';

/**
 * Servicio de productos.
 * Centraliza todas las llamadas API relacionadas con productos.
 * Buenas prácticas:
 * - Funciones específicas y tipadas
 * - Manejo de errores delegado al interceptor de Axios
 * - Fácil de testear y reutilizar
 */

export const productosService = {
    // Obtener todos los productos
    getAll: async (): Promise<Producto[]> => {
        const { data } = await api.get<Producto[]>('/productos');
        return data;
    },

    // Obtener producto por ID
    getById: async (id: number): Promise<Producto> => {
        const { data } = await api.get<Producto>(`/productos/${id}`);
        return data;
    },

    // Crear producto
    create: async (producto: Omit<Producto, 'id'>): Promise<Producto> => {
        const { data } = await api.post<Producto>('/productos', producto);
        return data;
    },

    // Actualizar producto
    update: async (id: number, producto: Omit<Producto, 'id'>): Promise<Producto> => {
        const { data } = await api.put<Producto>(`/productos/${id}`, producto);
        return data;
    },

    // Eliminar producto
    delete: async (id: number): Promise<void> => {
        await api.delete(`/productos/${id}`);
    },

    // Buscar por nombre
    search: async (nombre: string): Promise<Producto[]> => {
        const { data } = await api.get<Producto[]>('/productos/buscar', {
            params: { nombre },
        });
        return data;
    },

    // Obtener productos con stock bajo
    getStockBajo: async (maximo: number = 10): Promise<Producto[]> => {
        const { data } = await api.get<Producto[]>('/productos/stock-bajo', {
            params: { maximo },
        });
        return data;
    },
};

// Servicio de categorías (lo usaremos en el formulario de productos)
export const categoriasService = {
    getAll: async (): Promise<Categoria[]> => {
        const { data } = await api.get<Categoria[]>('/categorias');
        return data;
    },
};