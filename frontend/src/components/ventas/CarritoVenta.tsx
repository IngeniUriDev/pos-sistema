import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { productosService } from '../../api/productosService';
import { ventasService } from '../../api/ventasService';
import type { Producto, DetalleVenta, VentaRequest } from '../../types';

interface CarritoVentaProps {
  onVentaCompletada: () => void;
  onCancel: () => void;
}

interface ItemCarrito extends DetalleVenta {
  producto: Producto;
}

export default function CarritoVenta({ onVentaCompletada, onCancel }: CarritoVentaProps) {
  const [productos, setProductos] = useState<Producto[]>([]);
  const [carrito, setCarrito] = useState<ItemCarrito[]>([]);
  const [busqueda, setBusqueda] = useState('');
  const [metodoPago, setMetodoPago] = useState<'EFECTIVO' | 'TARJETA_CREDITO' | 'TARJETA_DEBITO' | 'TRANSFERENCIA'>('EFECTIVO');
  const [procesando, setProcesando] = useState(false);

  useEffect(() => {
    cargarProductos();
  }, []);

  const cargarProductos = async () => {
    try {
      const data = await productosService.getAll();
      setProductos(data.filter(p => p.stock > 0)); // Solo productos con stock
    } catch (error) {
      toast.error('Error al cargar productos');
    }
  };

  const agregarAlCarrito = (producto: Producto) => {
    const existe = carrito.find(item => item.productoId === producto.id);

    if (existe) {
      if (existe.cantidad >= producto.stock) {
        toast.error(`Stock insuficiente para ${producto.nombre}`);
        return;
      }
      setCarrito(carrito.map(item =>
        item.productoId === producto.id
          ? { ...item, cantidad: item.cantidad + 1 }
          : item
      ));
    } else {
      setCarrito([...carrito, { productoId: producto.id, cantidad: 1, producto }]);
    }

    toast.success(`${producto.nombre} agregado al carrito`);
  };

  const actualizarCantidad = (productoId: number, cantidad: number) => {
    if (cantidad <= 0) {
      eliminarDelCarrito(productoId);
      return;
    }

    const producto = productos.find(p => p.id === productoId);
    if (producto && cantidad > producto.stock) {
      toast.error(`Stock máximo: ${producto.stock} unidades`);
      return;
    }

    setCarrito(carrito.map(item =>
      item.productoId === productoId ? { ...item, cantidad } : item
    ));
  };

  const eliminarDelCarrito = (productoId: number) => {
    setCarrito(carrito.filter(item => item.productoId !== productoId));
  };

  const calcularTotales = () => {
    const subtotal = carrito.reduce((sum, item) => {
      return sum + (item.producto.precio * item.cantidad);
    }, 0);
    const impuesto = subtotal * 0.16;
    const total = subtotal + impuesto;
    return { subtotal, impuesto, total };
  };

  const procesarVenta = async () => {
    if (carrito.length === 0) {
      toast.error('El carrito está vacío');
      return;
    }

    if (!window.confirm('¿Confirmar venta?')) {
      return;
    }

    try {
      setProcesando(true);

      const ventaRequest: VentaRequest = {
        metodoPago,
        productos: carrito.map(item => ({
          productoId: item.productoId,
          cantidad: item.cantidad,
        })),
      };

      await ventasService.create(ventaRequest);
      toast.success('¡Venta registrada exitosamente!');
      onVentaCompletada();
    } catch (error) {
      toast.error('Error al procesar la venta');
    } finally {
      setProcesando(false);
    }
  };

  const { subtotal, impuesto, total } = calcularTotales();

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
      {/* Lista de productos disponibles */}
      <div className="lg:col-span-2 bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <h2 className="text-xl font-bold text-gray-900 mb-4">Productos Disponibles</h2>

        {/* Buscador */}
        <input
          type="text"
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          placeholder="Buscar producto..."
          className="w-full px-4 py-2 border border-gray-300 rounded-lg mb-4 focus:ring-2 focus:ring-blue-500"
        />

        {/* Grid de productos */}
        <div className="grid grid-cols-2 md:grid-cols-3 gap-4 max-h-[600px] overflow-y-auto">
          {productos
            .filter(p => p.nombre.toLowerCase().includes(busqueda.toLowerCase()))
            .map(producto => (
              <button
                key={producto.id}
                onClick={() => agregarAlCarrito(producto)}
                className="p-4 border-2 border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition text-left"
              >
                <div className="font-medium text-gray-900 mb-1">{producto.nombre}</div>
                <div className="text-sm text-gray-500 mb-2">{producto.categoriaNombre}</div>
                <div className="flex items-center justify-between">
                  <span className="text-lg font-bold text-green-600">
                    ${producto.precio.toFixed(2)}
                  </span>
                  <span className="text-xs bg-gray-100 px-2 py-1 rounded">
                    Stock: {producto.stock}
                  </span>
                </div>
              </button>
            ))}
        </div>
      </div>

      {/* Carrito */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <h2 className="text-xl font-bold text-gray-900 mb-4">Carrito de Venta</h2>

        {carrito.length === 0 ? (
          <div className="text-center py-12 text-gray-500">
            <span className="text-4xl mb-2 block">🛒</span>
            Carrito vacío
          </div>
        ) : (
          <>
            {/* Items del carrito */}
            <div className="space-y-3 mb-4 max-h-[400px] overflow-y-auto">
              {carrito.map(item => (
                <div key={item.productoId} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div className="flex-1">
                    <div className="font-medium text-gray-900">{item.producto.nombre}</div>
                    <div className="text-sm text-gray-500">
                      ${item.producto.precio.toFixed(2)} c/u
                    </div>
                  </div>

                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => actualizarCantidad(item.productoId, item.cantidad - 1)}
                      className="w-8 h-8 bg-gray-200 hover:bg-gray-300 rounded flex items-center justify-center"
                    >
                      -
                    </button>
                    <span className="w-8 text-center font-medium">{item.cantidad}</span>
                    <button
                      onClick={() => actualizarCantidad(item.productoId, item.cantidad + 1)}
                      className="w-8 h-8 bg-gray-200 hover:bg-gray-300 rounded flex items-center justify-center"
                    >
                      +
                    </button>
                    <button
                      onClick={() => eliminarDelCarrito(item.productoId)}
                      className="ml-2 text-red-600 hover:text-red-800"
                    >
                      🗑️
                    </button>
                  </div>
                </div>
              ))}
            </div>

            {/* Totales */}
            <div className="border-t border-gray-200 pt-4 space-y-2 mb-4">
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Subtotal:</span>
                <span className="font-medium">${subtotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">IVA (16%):</span>
                <span className="font-medium">${impuesto.toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-lg font-bold">
                <span>Total:</span>
                <span className="text-green-600">${total.toFixed(2)}</span>
              </div>
            </div>

            {/* Método de pago */}
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Método de Pago
              </label>
              <select
                value={metodoPago}
                onChange={(e) => setMetodoPago(e.target.value as any)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              >
                <option value="EFECTIVO">Efectivo</option>
                <option value="TARJETA_CREDITO">Tarjeta de Crédito</option>
                <option value="TARJETA_DEBITO">Tarjeta de Débito</option>
                <option value="TRANSFERENCIA">Transferencia</option>
              </select>
            </div>

            {/* Botones de acción */}
            <div className="space-y-2">
              <button
                onClick={procesarVenta}
                disabled={procesando || carrito.length === 0}
                className="w-full py-3 bg-green-600 hover:bg-green-700 text-white font-bold rounded-lg transition disabled:opacity-50"
              >
                {procesando ? 'Procesando...' : '💰 Procesar Venta'}
              </button>
              <button
                onClick={onCancel}
                className="w-full py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition"
              >
                Cancelar
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}