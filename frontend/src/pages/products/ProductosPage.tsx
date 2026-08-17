import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import DataTable from '../../components/ui/DataTable';
import Modal from '../../components/ui/Modal';
import ProductoForm from '../../components/products/ProductoForm';
import { productosService } from '../../api/productosService';
import type { Producto } from '../../types';

export default function ProductosPage() {
    const [productos, setProductos] = useState<Producto[]>([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [modalOpen, setModalOpen] = useState(false);
    const [editingProducto, setEditingProducto] = useState<Producto | null>(null);
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        loadProductos();
    }, []);

    const loadProductos = async () => {
        try {
            setLoading(true);
            const data = await productosService.getAll();
            setProductos(data);
        } catch (error) {
            toast.error('Error al cargar productos');
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async (term: string) => {
        setSearchTerm(term);
        if (term.trim() === '') {
            loadProductos();
            return;
        }
        try {
            const data = await productosService.search(term);
            setProductos(data);
        } catch (error) {
            toast.error('Error al buscar productos');
        }
    };

    const handleCreate = () => {
        setEditingProducto(null);
        setModalOpen(true);
    };

    const handleEdit = (producto: Producto) => {
        setEditingProducto(producto);
        setModalOpen(true);
    };

    const handleDelete = async (producto: Producto) => {
        if (!window.confirm(`¿Estás seguro de eliminar "${producto.nombre}"?`)) {
            return;
        }
        try {
            await productosService.delete(producto.id);
            toast.success('Producto eliminado');
            loadProductos();
        } catch (error) {
            toast.error('Error al eliminar producto');
        }
    };

    const handleSubmit = async (data: Omit<Producto, 'id'>) => {
        try {
            setSaving(true);
            if (editingProducto) {
                await productosService.update(editingProducto.id, data);
                toast.success('Producto actualizado');
            } else {
                await productosService.create(data);
                toast.success('Producto creado');
            }
            setModalOpen(false);
            loadProductos();
        } catch (error) {
            toast.error('Error al guardar producto');
        } finally {
            setSaving(false);
        }
    };

    const columns = [
        {
            header: 'ID',
            accessor: (p: Producto) => <span className="text-gray-500">#{p.id}</span>,
        },
        {
            header: 'Nombre',
            accessor: (p: Producto) => (
                <div>
                    <div className="font-medium text-gray-900">{p.nombre}</div>
                    {p.descripcion && (
                        <div className="text-xs text-gray-500">{p.descripcion}</div>
                    )}
                </div>
            ),
        },
        {
            header: 'Categoría',
            accessor: (p: Producto) => (
                <span className="px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 rounded-full">
          {p.categoriaNombre || 'Sin categoría'}
        </span>
            ),
        },
        {
            header: 'Precio',
            accessor: (p: Producto) => (
                <span className="font-semibold text-green-600">
          ${p.precio.toFixed(2)}
        </span>
            ),
        },
        {
            header: 'Stock',
            accessor: (p: Producto) => (
                <span
                    className={`px-2 py-1 text-xs font-medium rounded-full ${
                        p.stock <= 10
                            ? 'bg-red-100 text-red-800'
                            : p.stock <= 50
                                ? 'bg-yellow-100 text-yellow-800'
                                : 'bg-green-100 text-green-800'
                    }`}
                >
          {p.stock} unidades
        </span>
            ),
        },
    ];

    return (
        <div>
            {/* Header */}
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900">Productos</h1>
                    <p className="text-gray-500 mt-1">
                        Gestión de inventario y productos
                    </p>
                </div>
                <button
                    onClick={handleCreate}
                    className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition flex items-center"
                >
                    <span className="mr-2">+</span>
                    Nuevo Producto
                </button>
            </div>

            {/* Search Bar */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4 mb-6">
                <div className="flex items-center space-x-4">
                    <div className="flex-1">
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => handleSearch(e.target.value)}
                            placeholder="Buscar productos por nombre..."
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        />
                    </div>
                    <button
                        onClick={loadProductos}
                        className="px-4 py-2 text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg transition"
                    >
                        🔄 Refrescar
                    </button>
                </div>
            </div>

            {/* Table */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                <DataTable
                    data={productos}
                    columns={columns}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                    loading={loading}
                    emptyMessage="No hay productos registrados"
                />
            </div>

            {/* Modal Form */}
            <Modal
                isOpen={modalOpen}
                onClose={() => setModalOpen(false)}
                title={editingProducto ? 'Editar Producto' : 'Nuevo Producto'}
                size="md"
            >
                <ProductoForm
                    producto={editingProducto}
                    onSubmit={handleSubmit}
                    onCancel={() => setModalOpen(false)}
                    loading={saving}
                />
            </Modal>
        </div>
    );
}