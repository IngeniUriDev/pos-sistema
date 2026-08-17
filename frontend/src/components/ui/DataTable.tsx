interface Column<T> {
    header: string;
    accessor: keyof T | ((item: T) => React.ReactNode);
    className?: string;
}

interface DataTableProps<T> {
    data: T[];
    columns: Column<T>[];
    onEdit?: (item: T) => void;
    onDelete?: (item: T) => void;
    loading?: boolean;
    emptyMessage?: string;
}

/**
 * Componente de tabla reutilizable y tipado.
 * ️ DRY: Lo usaremos para productos, categorías, ventas, etc.
 */
export default function DataTable<T extends { id: number }>({
                                                                data,
                                                                columns,
                                                                onEdit,
                                                                onDelete,
                                                                loading,
                                                                emptyMessage = 'No hay datos disponibles',
                                                            }: DataTableProps<T>) {
    if (loading) {
        return (
            <div className="flex items-center justify-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>
        );
    }

    if (data.length === 0) {
        return (
            <div className="text-center py-12 text-gray-500">
                <span className="text-4xl mb-2 block">📭</span>
                {emptyMessage}
            </div>
        );
    }

    return (
        <div className="overflow-x-auto">
            <table className="w-full">
                <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                    {columns.map((col, idx) => (
                        <th
                            key={idx}
                            className={`px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider ${col.className || ''}`}
                        >
                            {col.header}
                        </th>
                    ))}
                    {(onEdit || onDelete) && (
                        <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Acciones
                        </th>
                    )}
                </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                {data.map((item) => (
                    <tr key={item.id} className="hover:bg-gray-50 transition">
                        {columns.map((col, idx) => (
                            <td key={idx} className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                {typeof col.accessor === 'function'
                                    ? col.accessor(item)
                                    : String(item[col.accessor] ?? '')}
                            </td>
                        ))}
                        {(onEdit || onDelete) && (
                            <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                {onEdit && (
                                    <button
                                        onClick={() => onEdit(item)}
                                        className="text-blue-600 hover:text-blue-900 mr-3"
                                    >
                                        ✏️ Editar
                                    </button>
                                )}
                                {onDelete && (
                                    <button
                                        onClick={() => onDelete(item)}
                                        className="text-red-600 hover:text-red-900"
                                    >
                                        🗑️ Eliminar
                                    </button>
                                )}
                            </td>
                        )}
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}