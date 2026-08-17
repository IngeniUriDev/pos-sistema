import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import DataTable from '../../components/ui/DataTable';
import Modal from '../../components/ui/Modal';
import { categoriasService } from '../../api/categoriasService';
import type { Categoria } from '../../types';

export default function CategoriasPage() {
    const [categorias, setCategorias] = useState<Categoria[]>([]);
    const [loading, setLoading] = useState(true);
    const [modalOpen, setModalOpen] = useState(false);
    const [editingCategoria, setEditingCategoria] = useState<Categoria | null>(null);
    const [saving, setSaving] = useState(false);

    const [form, setForm] = useState({ nombre: '', descripcion: '' });

    useEffect(() => {
        loadCategorias();
    }, []);

    const loadCategorias = async () => {
        try {
            setLoading(true);
            const data = await categoriasService.getAll();
            setCategorias(data);
        } catch (error) {
            toast.error('Error al cargar categorías');
        } finally {
            setLoading(false);
        }
    };

    const openModal = (categoria?: Categoria) => {
        if (categoria) {
            setEditingCategoria(categoria);
            setForm({ nombre: categoria.nombre, descripcion: categoria.descripcion || '' });
        } else {
            setEditingCategoria(null);
            setForm({ nombre: '', descripcion: '' });
        }
        setModalOpen(true);
    };

    const handleDelete = async (categoria: Categoria) => {
        if (!window.confirm(`¿Eliminar la categoría "${categoria.nombre}"?`)) return;
        try {
            await categoriasService.delete(categoria.id);
            toast.success('Categoría eliminada');
            loadCategorias();
        } catch (error) {
            toast.error('Error al eliminar');
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!form.nombre.trim()) {
            toast.error('El nombre es obligatorio');
            return;
        }

        try {
            setSaving(true);
            if (editingCategoria) {
                await categoriasService.update(editingCategoria.id, form);
                toast.success('Categoría actualizada');
            } else {
                await categoriasService.create(form);
                toast.success('Categoría creada');
            }
            setModalOpen(false);
            loadCategorias();
        } catch (error) {
            toast.error('Error al guardar');
        } finally {
            setSaving(false);
        }
    };

    const columns = [
        { header: 'ID', accessor: (c: Categoria) => <span className="text-gray-500">#{c.id}</span> },
        {
            header: 'Nombre',
            accessor: (c: Categoria) => <span className="font-medium text-gray-900">{c.nombre}</span>
        },
        {
            header: 'Descripción',
            accessor: (c: Categoria) => <span className="text-gray-600">{c.descripcion || '-'}</span>
        },
    ];

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900">Categorías</h1>
                    <p className="text-gray-500 mt-1">Organiza tus productos por categorías</p>
                </div>
                <button
                    onClick={() => openModal()}
                    className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition flex items-center"
                >
                    <span className="mr-2">+</span>
                    Nueva Categoría
                </button>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                <DataTable
                    data={categorias}
                    columns={columns}
                    onEdit={openModal}
                    onDelete={handleDelete}
                    loading={loading}
                    emptyMessage="No hay categorías registradas"
                />
            </div>

            <Modal
                isOpen={modalOpen}
                onClose={() => setModalOpen(false)}
                title={editingCategoria ? 'Editar Categoría' : 'Nueva Categoría'}
                size="sm"
            >
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Nombre *</label>
                        <input
                            type="text"
                            value={form.nombre}
                            onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
                        <textarea
                            value={form.descripcion}
                            onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                            rows={3}
                        />
                    </div>
                    <div className="flex justify-end space-x-3 pt-4 border-t">
                        <button type="button" onClick={() => setModalOpen(false)} className="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg">
                            Cancelar
                        </button>
                        <button type="submit" disabled={saving} className="px-4 py-2 text-white bg-blue-600 rounded-lg disabled:opacity-50">
                            {saving ? 'Guardando...' : 'Guardar'}
                        </button>
                    </div>
                </form>
            </Modal>
        </div>
    );
}