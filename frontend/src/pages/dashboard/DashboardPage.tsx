import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { ventasService } from '../../api/ventasService';
import { productosService } from '../../api/productosService';
import {formatCurrency} from "../../utils/format.ts";

export default function DashboardPage() {
    const [stats, setStats] = useState({
        totalVentas: 0,
        ingresosTotales: 0,
        productosBajoStock: 0,
        ventasHoy: 0,
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadRealStats();
    }, []);

    const loadRealStats = async () => {
        try {
            setLoading(true);

            // Llamadas en paralelo para mejor rendimiento (Promise.all)
            const [ventas, productosBajoStock] = await Promise.all([
                ventasService.getAll(),
                productosService.getStockBajo(10),
            ]);

            const ingresosTotales = ventas.reduce((sum, v) => sum + v.total, 0);
            const ventasHoy = ventas.filter(v => {
                const hoy = new Date().toDateString();
                return new Date(v.fechaVenta).toDateString() === hoy;
            }).length;

            setStats({
                totalVentas: ventas.length,
                ingresosTotales,
                productosBajoStock: productosBajoStock.length,
                ventasHoy,
            });
        } catch (error) {
            toast.error('Error al cargar estadísticas del dashboard');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>
        );
    }

    const cards = [
        { title: 'Total de Ventas', value: stats.totalVentas, icon: '', color: 'bg-blue-500', link: '/ventas' },
        { title: 'Ingresos Totales', value: formatCurrency(stats.ingresosTotales), icon: '📈', color: 'bg-green-500', link: '/ventas' },
        { title: 'Stock Bajo', value: stats.productosBajoStock, icon: '⚠️', color: 'bg-red-500', link: '/productos' },
        { title: 'Ventas Hoy', value: stats.ventasHoy, icon: '📅', color: 'bg-purple-500', link: '/ventas' },
    ];

    return (
        <div>
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
                <p className="text-gray-500 mt-1">Resumen en tiempo real de tu negocio</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                {cards.map((card, index) => (
                    <Link
                        to={card.link}
                        key={index}
                        className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition cursor-pointer"
                    >
                        <div className="flex items-center justify-between mb-4">
                            <div className={`${card.color} w-12 h-12 rounded-lg flex items-center justify-center text-2xl`}>
                                {card.icon}
                            </div>
                        </div>
                        <h3 className="text-2xl font-bold text-gray-900 mb-1">{card.value}</h3>
                        <p className="text-sm text-gray-500">{card.title}</p>
                    </Link>
                ))}
            </div>

            {/* Acciones Rápidas */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                <h2 className="text-lg font-bold text-gray-900 mb-4">Acciones Rápidas</h2>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <Link to="/ventas" className="p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-green-500 hover:bg-green-50 transition text-center">
                        <span className="text-3xl mb-2 block">💰</span>
                        <span className="font-medium text-gray-700">Nueva Venta</span>
                    </Link>
                    <Link to="/productos" className="p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition text-center">
                        <span className="text-3xl mb-2 block"></span>
                        <span className="font-medium text-gray-700">Gestionar Productos</span>
                    </Link>
                    <Link to="/categorias" className="p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-purple-500 hover:bg-purple-50 transition text-center">
                        <span className="text-3xl mb-2 block">🏷️</span>
                        <span className="font-medium text-gray-700">Categorías</span>
                    </Link>
                    <Link to="/reportes" className="p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-yellow-500 hover:bg-yellow-50 transition text-center">
                        <span className="text-3xl mb-2 block">📥</span>
                        <span className="font-medium text-gray-700">Reportes</span>
                    </Link>
                </div>
            </div>
        </div>
    );
}