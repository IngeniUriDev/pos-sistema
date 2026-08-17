import { create } from 'zustand';
import api from '../api/axios';
import type { AuthResponse, LoginRequest, Usuario } from '../types';

/**
 * Store de autenticación con Zustand.
 *
 * ¿Por qué Zustand y no Redux o Context?
 * - Menos boilerplate que Redux
 * - Más performante que Context (no re-renderiza toda la app)
 * - Tipado nativo con TypeScript
 */

interface AuthState {
    usuario: Usuario | null;
    token: string | null;
    isAuthenticated: boolean;
    login: (credentials: LoginRequest) => Promise<void>;
    logout: () => void;
    cargarUsuario: () => Promise<void>;
}

export const useAuthStore = create<AuthState>((set) => ({
    usuario: null,
    token: localStorage.getItem('token'),
    isAuthenticated: !!localStorage.getItem('token'),

    login: async (credentials) => {
        try {
            const { data } = await api.post<AuthResponse>('/auth/login', credentials);
            localStorage.setItem('token', data.token);
            set({ token: data.token, isAuthenticated: true });
        } catch (error) {
            throw error; // El interceptor ya mostró el toast
        }
    },

    logout: () => {
        localStorage.removeItem('token');
        set({ usuario: null, token: null, isAuthenticated: false });
    },

    cargarUsuario: async () => {
        // TODO: Implementar endpoint GET /api/auth/me en el backend
        // Por ahora, decodificamos el JWT para obtener datos básicos
    },
}));