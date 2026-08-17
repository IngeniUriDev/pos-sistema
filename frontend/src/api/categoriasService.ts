import api from './axios';
import type { Categoria } from '../types';

export const categoriasService = {
    getAll: async (): Promise<Categoria[]> => {
        const { data } = await api.get<Categoria[]>('/categorias');
        return data;
    },

    create: async (categoria: Omit<Categoria, 'id'>): Promise<Categoria> => {
        const { data } = await api.post<Categoria>('/categorias', categoria);
        return data;
    },

    update: async (id: number, categoria: Omit<Categoria, 'id'>): Promise<Categoria> => {
        const { data } = await api.put<Categoria>(`/categorias/${id}`, categoria);
        return data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/categorias/${id}`);
    },
};