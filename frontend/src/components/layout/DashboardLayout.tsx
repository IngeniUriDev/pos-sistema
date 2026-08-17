import { useState } from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../stores/authStore';

export default function DashboardLayout() {
    const [sidebarOpen, setSidebarOpen] = useState(true);
    const location = useLocation();
    const navigate = useNavigate();
    const { logout, usuario } = useAuthStore();

    const menuItems = [
        { path: '/dashboard', label: 'Dashboard', icon: '📊' },
        { path: '/productos', label: 'Productos', icon: '📦' },
        { path: '/categorias', label: 'Categorías', icon: '🏷️' },
        { path: '/ventas', label: 'Ventas', icon: '💰' },
        { path: '/reportes', label: 'Reportes', icon: '📈' },
    ];

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Sidebar */}
            <aside
                className={`fixed top-0 left-0 z-40 h-screen transition-transform ${
                    sidebarOpen ? 'translate-x-0' : '-translate-x-full'
                } bg-white border-r border-gray-200 w-64`}
            >
                <div className="h-full flex flex-col">
                    {/* Logo */}
                    <div className="flex items-center justify-center h-16 border-b border-gray-200">
                        <h1 className="text-xl font-bold text-blue-600">🛒 POS System</h1>
                    </div>

                    {/* Menú */}
                    <nav className="flex-1 px-3 py-4 overflow-y-auto">
                        <ul className="space-y-2">
                            {menuItems.map((item) => {
                                const isActive = location.pathname === item.path;
                                return (
                                    <li key={item.path}>
                                        <Link
                                            to={item.path}
                                            className={`flex items-center px-4 py-3 rounded-lg transition ${
                                                isActive
                                                    ? 'bg-blue-600 text-white'
                                                    : 'text-gray-700 hover:bg-gray-100'
                                            }`}
                                        >
                                            <span className="mr-3 text-xl">{item.icon}</span>
                                            <span className="font-medium">{item.label}</span>
                                        </Link>
                                    </li>
                                );
                            })}
                        </ul>
                    </nav>

                    {/* User info & Logout */}
                    <div className="p-4 border-t border-gray-200">
                        <button
                            onClick={handleLogout}
                            className="w-full flex items-center justify-center px-4 py-2 text-red-600 bg-red-50 hover:bg-red-100 rounded-lg transition"
                        >
                            <span className="mr-2">🚪</span>
                            Cerrar Sesión
                        </button>
                    </div>
                </div>
            </aside>

            {/* Main content */}
            <div className={`transition-all ${sidebarOpen ? 'ml-64' : 'ml-0'}`}>
                {/* Header */}
                <header className="bg-white border-b border-gray-200 h-16 flex items-center justify-between px-6">
                    <button
                        onClick={() => setSidebarOpen(!sidebarOpen)}
                        className="p-2 rounded-lg hover:bg-gray-100"
                    >
                        <span className="text-2xl">☰</span>
                    </button>

                    <div className="flex items-center space-x-4">
                        <div className="text-right">
                            <p className="text-sm font-medium text-gray-900">
                                {usuario?.nombreCompleto || 'Admin'}
                            </p>
                            <p className="text-xs text-gray-500">Administrador</p>
                        </div>
                        <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center text-white font-bold">
                            A
                        </div>
                    </div>
                </header>

                {/* Page content */}
                <main className="p-6">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}