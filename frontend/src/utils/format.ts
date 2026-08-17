/**
 * Utilidades de formateo.
 * Centraliza el formateo de moneda y fechas para consistencia en toda la app.
 */

/**
 * Formatea un número como moneda mexicana (MXN).
 * Ejemplo: 1234.56 → "$1,234.56"
 */
export const formatCurrency = (amount: number): string => {
    return new Intl.NumberFormat('es-MX', {
        style: 'currency',
        currency: 'MXN',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
    }).format(amount);
};

/**
 * Formatea una fecha en formato legible.
 * Ejemplo: "2026-08-14T10:30:00" → "14/08/2026 10:30"
 */
export const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('es-MX', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
    }).format(date);
};

/**
 * Formatea solo la fecha (sin hora).
 */
export const formatDateShort = (dateString: string): string => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('es-MX', {
        year: 'numeric',
        month: 'short',
        day: '2-digit',
    }).format(date);
};