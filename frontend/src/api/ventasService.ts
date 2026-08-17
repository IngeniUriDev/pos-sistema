import api from './axios';
import type { Venta, VentaRequest } from '../types';

/**
 * Servicio de ventas.
 * Maneja todas las operaciones relacionadas con ventas.
 */

export const ventasService = {
    // Crear nueva venta
    create: async (venta: VentaRequest): Promise<Venta> => {
        const { data } = await api.post<Venta>('/ventas', venta);
        return data;
    },

    // Obtener todas las ventas
    getAll: async (): Promise<Venta[]> => {
        const { data } = await api.get<Venta[]>('/ventas');
        return data;
    },

    // Obtener venta por ID
    getById: async (id: number): Promise<Venta> => {
        const { data } = await api.get<Venta>(`/ventas/${id}`);
        return data;
    },

    // Obtener ventas del día
    getVentasDelDia: async (): Promise<Venta[]> => {
        const { data } = await api.get<Venta[]>('/ventas/hoy');
        return data;
    },

    // Obtener ventas por rango de fechas
    getVentasPorRango: async (inicio: string, fin: string): Promise<Venta[]> => {
        const { data } = await api.get<Venta[]>('/ventas/rango', {
            params: { inicio, fin },
        });
        return data;
    },
};

// Servicio para descargar el reporte Excel
export const reportesService = {
    descargarVentasExcel: async (): Promise<void> => {
        const response = await api.get('/reportes/ventas/excel', {
            responseType: 'blob',
        });

        // Crear URL del blob y descargar
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `reporte_ventas_${Date.now()}.xlsx`);
        document.body.appendChild(link);
        link.click();
        link.remove();
        window.URL.revokeObjectURL(url);
    },
};