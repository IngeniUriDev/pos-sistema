import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';

// 1. Imports de páginas y componentes
import LoginPage from './pages/auth/LoginPage';
import DashboardLayout from './components/layout/DashboardLayout';
import DashboardPage from './pages/dashboard/DashboardPage';
import ProductosPage from './pages/products/ProductosPage';
import VentasPage from './pages/ventas/VentasPage';
import CategoriasPage from './pages/categorias/CategoriasPage'; // Importamos el real
import ProtectedRoute from './components/layout/ProtectedRoute';

// 2. Solo declaramos el placeholder para Reportes (ya que no lo hemos creado aún)
const ReportesPage = () => (
    <div className="text-2xl text-gray-500 flex items-center justify-center h-64">
        📈 Reportes - Próximamente
    </div>
);

function App() {
    return (
        <>
            <BrowserRouter>
                <Routes>
                    {/* Ruta pública */}
                    <Route path="/login" element={<LoginPage />} />

                    {/* Rutas protegidas con Layout */}
                    <Route
                        path="/"
                        element={
                            <ProtectedRoute>
                                <DashboardLayout />
                            </ProtectedRoute>
                        }
                    >
                        <Route index element={<Navigate to="/dashboard" replace />} />
                        <Route path="dashboard" element={<DashboardPage />} />
                        <Route path="productos" element={<ProductosPage />} />
                        <Route path="categorias" element={<CategoriasPage />} /> {/* Solo una vez */}
                        <Route path="ventas" element={<VentasPage />} />
                        <Route path="reportes" element={<ReportesPage />} />
                    </Route>

                    {/* Redirección por defecto */}
                    <Route path="*" element={<Navigate to="/dashboard" replace />} />
                </Routes>
            </BrowserRouter>

            <Toaster position="top-right" />
        </>
    );
}

export default App;