import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { categoriasService } from '../../api/productosService';
import type { Producto, Categoria } from '../../types';

interface ProductoFormProps {
    producto?: Producto | null;
    onSubmit: (data: Omit<Producto, 'id'>) => Promise<void>;
    onCancel: () => void;
    loading?: boolean;
}

export default function ProductoForm({ producto, onSubmit, onCancel, loading }: ProductoFormProps) {
    const [categorias, setCategorias] = useState<Categoria[]>([]);
    const [form, setForm] = useState({
        nombre: '',
        descripcion: '',
        precio: 0,
        stock: 0,
        categoriaId: 0,
    });

    useEffect(() => {
        loadCategorias();
        if (producto) {
            setForm({
                nombre: producto.nombre,
                descripcion: producto.descripcion || '',
                precio: producto.precio,
                stock: producto.stock,
                categoriaId: producto.categoriaId,
            });
        }
    }, [producto]);

    const loadCategorias = async () => {
        try {
            const data = await categoriasService.getAll();
            setCategorias(data);
            if (data.length > 0 && !producto) {
                setForm(prev => ({ ...prev, categoriaId: data[0].id }));
            }
        } catch (error) {
            toast.error('Error al cargar categorías');
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (form.precio <= 0) {
            toast.error('El precio debe ser mayor a 0');
            return;
        }

        if (form.stock < 0) {
            toast.error('El stock no puede ser negativo');
            return;
        }

        await onSubmit(form);
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                    Nombre del Producto *
                </label>
                <input
                    type="text"
                    value={form.nombre}
                    onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                    placeholder="Ej: Laptop Dell XPS 15"
                />
            </div>

            <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                    Descripción
                </label>
                <textarea
                    value={form.descripcion}
                    onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    rows={3}
                    placeholder="Descripción del producto (opcional)"
                />
            </div>

            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Precio *
                    </label>
                    <input
                        type="number"
                        step="0.01"
                        min="0.01"
                        value={form.precio}
                        onChange={(e) => setForm({ ...form, precio: parseFloat(e.target.value) || 0 })}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        required
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Stock *
                    </label>
                    <input
                        type="number"
                        min="0"
                        value={form.stock}
                        onChange={(e) => setForm({ ...form, stock: parseInt(e.target.value) || 0 })}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        required
                    />
                </div>
            </div>

            <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                    Categoría *
                </label>
                <select
                    value={form.categoriaId}
                    onChange={(e) => setForm({ ...form, categoriaId: parseInt(e.target.value) })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                >
                    <option value={0}>Seleccionar categoría</option>
                    {categorias.map((cat) => (
                        <option key={cat.id} value={cat.id}>
                            {cat.nombre}
                        </option>
                    ))}
                </select>
            </div>

            <div className="flex justify-end space-x-3 pt-4 border-t border-gray-200">
                <button
                    type="button"
                    onClick={onCancel}
                    className="px-4 py-2 text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg transition"
                >
                    Cancelar
                </button>
                <button
                    type="submit"
                    disabled={loading}
                    className="px-4 py-2 text-white bg-blue-600 hover:bg-blue-700 rounded-lg transition disabled:opacity-50"
                >
                    {loading ? 'Guardando...' : producto ? 'Actualizar' : 'Crear'}
                </button>
            </div>
        </form>
    );
}