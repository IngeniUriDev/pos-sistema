import {Component} from 'react';
import type {ErrorInfo, ReactNode} from 'react';

interface Props {
    children: ReactNode;
    fallback?: ReactNode;
}

interface State {
    hasError: boolean;
    error: Error | null;
}

/**
 * Error Boundary: Captura errores en componentes hijos
 * y muestra una UI de error en lugar de romper toda la app.
 *
 * ️ Solo puede ser class component (requerimiento de React)
 */
export default class ErrorBoundary extends Component<Props, State> {
    constructor(props: Props) {
        super(props);
        this.state = { hasError: false, error: null };
    }

    static getDerivedStateFromError(error: Error): State {
        return { hasError: true, error };
    }

    componentDidCatch(error: Error, errorInfo: ErrorInfo) {
        console.error('ErrorBoundary capturó un error:', error, errorInfo);
        // Aquí podrías enviar el error a un servicio como Sentry
    }

    render() {
        if (this.state.hasError) {
            return (
                this.props.fallback || (
                    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
                        <div className="bg-white rounded-xl shadow-lg p-8 max-w-md w-full text-center">
                            <div className="text-6xl mb-4">⚠️</div>
                            <h2 className="text-2xl font-bold text-gray-900 mb-2">
                                Algo salió mal
                            </h2>
                            <p className="text-gray-600 mb-4">
                                {this.state.error?.message || 'Error inesperado'}
                            </p>
                            <button
                                onClick={() => window.location.reload()}
                                className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
                            >
                                Recargar página
                            </button>
                        </div>
                    </div>
                )
            );
        }

        return this.props.children;
    }
}