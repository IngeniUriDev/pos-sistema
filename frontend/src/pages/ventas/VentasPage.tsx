import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import DataTable from '../../components/ui/DataTable';
import CarritoVenta from '../../components/ventas/CarritoVenta';
import { ventasService, reportesService } from '../../api/ventasService';
import { formatCurrency, formatDate } from '../../utils/format'; // ✅ IMPORT AGREGADO
import type { Venta } from '../../types';

export default function VentasPage() {
    const [ventas, setVentas] = useState<Venta[]>([]);
    const [loading, setLoading] = useState(true);
    const [mostrarCarrito, setMostrarCarrito] = useState(false);

    useEffect(() => {
        cargarVentas();
    }, []);

    const cargarVentas = async () => {
        try {
            setLoading(true);
            const data = await ventasService.getAll();
            setVentas(data);
        } catch (error) {
            toast.error('Error al cargar ventas');
        } finally {
            setLoading(false);
        }
    };

    const handleDescargarExcel = async () => {
        try {
            await reportesService.descargarVentasExcel();
            toast.success('Reporte descargado');
        } catch (error) {
            toast.error('Error al descargar reporte');
        }
    };

    // ✅ Array columns corregido (sin llaves duplicadas)
    const columns = [
        {
            header: 'ID',
            accessor: (v: Venta) => <span className="text-gray-500">#{v.id}</span>,
        },
        {
            header: 'Fecha',
            accessor: (v: Venta) => (
                <span className="text-sm">{formatDate(v.fechaVenta)}</span>
            ),
        },
        {
            header: 'Vendedor',
            accessor: (v: Venta) => (
                <span className="font-medium">{v.vendedorNombre}</span>
            ),
        },
        {
            header: 'Método de Pago',
            accessor: (v: Venta) => (
                <span className="px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 rounded-full">
                    {v.metodoPago.replace('_', ' ')}
                </span>
            ),
        },
        {
            header: 'Total',
            accessor: (v: Venta) => (
                <span className="font-bold text-green-600">
                    {formatCurrency(v.total)}
                </span>
            ),
        },
    ];

    // ✅ Calcular totales una sola vez (DRY)
    const totalIngresos = ventas.reduce((sum, v) => sum + v.total, 0);
    const ticketPromedio = ventas.length > 0 ? totalIngresos / ventas.length : 0;

    if (mostrarCarrito) {
        return (
            <div>
                <div className="mb-6">
                    <h1 className="text-3xl font-bold text-gray-900">Nueva Venta</h1>
                    <p className="text-gray-500 mt-1">Selecciona productos y procesa la venta</p>
                </div>

                <CarritoVenta
                    onVentaCompletada={() => {
                        setMostrarCarrito(false);
                        cargarVentas();
                    }}
                    onCancel={() => setMostrarCarrito(false)}
                />
            </div>
        );
    }

    return (
        <div>
            {/* Header */}
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900">Ventas</h1>
                    <p className="text-gray-500 mt-1">
                        Historial de ventas y registro de nuevas transacciones
                    </p>
                </div>
                <div className="flex space-x-3">
                    <button
                        onClick={handleDescargarExcel}
                        className="px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white font-medium rounded-lg transition flex items-center"
                    >
                        <span className="mr-2">📥</span>
                        Exportar Excel
                    </button>
                    <button
                        onClick={() => setMostrarCarrito(true)}
                        className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white font-medium rounded-lg transition flex items-center"
                    >
                        <span className="mr-2">+</span>
                        Nueva Venta
                    </button>
                </div>
            </div>

            {/* Stats - ✅ Estructura corregida */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                    <div className="text-sm text-gray-500 mb-1">Total de Ventas</div>
                    <div className="text-3xl font-bold text-gray-900">{ventas.length}</div>
                </div>
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                    <div className="text-sm text-gray-500 mb-1">Ingresos Totales</div>
                    <div className="text-3xl font-bold text-green-600">
                        {formatCurrency(totalIngresos)}
                    </div>
                </div>
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                    <div className="text-sm text-gray-500 mb-1">Ticket Promedio</div>
                    <div className="text-3xl font-bold text-blue-600">
                        {formatCurrency(ticketPromedio)}
                    </div>
                </div>
            </div>

            {/* Tabla de ventas */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                <DataTable
                    data={ventas}
                    columns={columns}
                    loading={loading}
                    emptyMessage="No hay ventas registradas"
                />
            </div>
        </div>
    );
}