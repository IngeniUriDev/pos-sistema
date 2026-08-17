import axios from 'axios';
import toast from 'react-hot-toast';

/**
 * Instancia configurada de Axios.
 *
 * Buenas prácticas aplicadas:
 * - Base URL centralizada (fácil de cambiar en producción)
 * - Interceptor de request: agrega token automáticamente
 * - Interceptor de response: maneja errores globalmente
 */
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor para agregar token en cada request
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Interceptor para manejar errores globalmente
api.interceptors.response.use(
    (response) => response,
    (error) => {
        // Manejo centralizado de errores
        if (error.response) {
            const { status, data } = error.response;

            switch (status) {
                case 401:
                    toast.error('Sesión expirada. Inicia sesión nuevamente.');
                    localStorage.removeItem('token');
                    window.location.href = '/login';
                    break;
                case 403:
                    toast.error('No tienes permisos para esta acción.');
                    break;
                case 404:
                    toast.error('Recurso no encontrado.');
                    break;
                case 500:
                    toast.error('Error del servidor. Intenta más tarde.');
                    break;
                default:
                    toast.error(data?.message || 'Ocurrió un error inesperado.');
            }
        } else {
            toast.error('No se pudo conectar con el servidor.');
        }

        return Promise.reject(error);
    }
);

export default api;