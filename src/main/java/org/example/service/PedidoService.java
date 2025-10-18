package org.example.service;

import org.example.dao.ClienteDAO;
import org.example.dao.PedidoDAO;
import org.example.dao.ProductoDAO;
import org.example.dto.PedidoDTO;
import org.example.dto.ProductoDTO;
import org.example.mapper.MapperUtil;
import org.example.model.Cliente;
import org.example.model.Pedido;
import org.example.model.Producto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PedidoService {
    private final ClienteDAO clienteDAO;
    private final ProductoDAO productoDAO;
    private final PedidoDAO pedidoDAO;

    public PedidoService(ClienteDAO clienteDAO, ProductoDAO productoDAO, PedidoDAO pedidoDAO) {
        this.clienteDAO = clienteDAO;
        this.productoDAO = productoDAO;
        this.pedidoDAO = pedidoDAO;
    }

    public Pedido crearPedidoDesdeDTO(PedidoDTO pedidoDTO) throws Exception {

        // Buscar cliente
        Cliente cliente = clienteDAO.buscarPorNombre(pedidoDTO.getClienteNombre());
        if (cliente == null) {
            throw new Exception("Cliente no encontrado");
        }
        List<ProductoDTO> productos = pedidoDTO.getProductos();

        List<String>nombreProductos= new ArrayList<>();
        String nombreProducto;
        for (ProductoDTO producto : productos) {
            nombreProducto=producto.getNombre();
            nombreProductos.add(nombreProducto);
        }

        // Buscar productos disponibles una sola vez
        List<Producto> productosDelPedido = productoDAO.buscarProductosPorNombres(nombreProductos);

        // Validar productos faltantes
        if (productos.size() != pedidoDTO.getProductos().size()) {
            throw new Exception("Uno o más productos no fueron encontrados");
        }

        double total=0;
        for (Producto p :productosDelPedido){
            total=total+p.getPrecio();
        }

        // Crear pedido
        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .productos(productosDelPedido)
                .fecha(LocalDate.now())
                .total(total)
                .build();

        // Guardar
        pedidoDAO.guardar(pedido);
        return pedido;
    }

    // Lista todos los pedidos
    public List<PedidoDTO> listarPedidos() {
        List<Pedido> pedidos = pedidoDAO.listar();
        return pedidos.stream()
                .map(MapperUtil::toPedidoDTO)
                .collect(Collectors.toList());
    }

    // Buscar un pedido por su ID
    public PedidoDTO buscarPedidoPorId(Long id) throws Exception {
        Pedido pedido = pedidoDAO.buscarPorId(id);
        if (pedido == null) {
            throw new Exception("Pedido no encontrado");
        }
        return MapperUtil.toPedidoDTO(pedido);
    }

    //Actualiza los pedidos
    /**
     * Actualiza un pedido existente, incluyendo cliente, productos, fecha, y recalcula el total.
     * @param id El ID del pedido a actualizar.
     * @param pedidoDTO El DTO con los datos actualizados (clienteNombre, productos y total sugerido).
     * @return El PedidoDTO actualizado, o null si el pedido original no existe.
     * @throws Exception Si el cliente o alguno de los productos no es encontrado.
     */
    public PedidoDTO actualizarPedido(Long id, PedidoDTO pedidoDTO) throws Exception {
        Pedido pedidoExistente = pedidoDAO.buscarPorId(id);

        if (pedidoExistente == null) {
            return null; // El Servlet manejará el error 404 Not Found
        }

        // --- 1. Buscar y Actualizar Cliente ---
        Cliente nuevoCliente = clienteDAO.buscarPorNombre(pedidoDTO.getClienteNombre());
        if (nuevoCliente == null) {
            throw new Exception("Cliente no encontrado para la actualización");
        }
        pedidoExistente.setCliente(nuevoCliente);

        // --- 2. Buscar y Actualizar Productos ---
        List<ProductoDTO> productosDTO = pedidoDTO.getProductos();

        // Extraer nombres para la búsqueda optimizada
        List<String> nombreProductos = productosDTO.stream()
                .map(ProductoDTO::getNombre)
                .collect(Collectors.toList());

        List<Producto> nuevosProductos = productoDAO.buscarProductosPorNombres(nombreProductos);

        // Validar que se encontraron todos los productos
        if (nuevosProductos == null || nuevosProductos.size() != productosDTO.size()) {
            throw new Exception("Uno o más productos no fueron encontrados para la actualización");
        }

        pedidoExistente.setProductos(nuevosProductos);

        // --- 3. Recalcular y Actualizar Total ---
        double nuevoTotal = nuevosProductos.stream()
                .mapToDouble(Producto::getPrecio)
                .sum();

        pedidoExistente.setTotal(nuevoTotal);

        // --- 4. Actualizar Fecha ---
        pedidoExistente.setFecha(LocalDate.now());

        // --- 5. Guardar Cambios ---
        pedidoDAO.actualizar(pedidoExistente); // Asume que el DAO usa 'merge()'

        return MapperUtil.toPedidoDTO(pedidoExistente);
    }
    //Elimina un producto por id
    public boolean eliminarPedido(Long id) {
        Pedido pedido = pedidoDAO.buscarPorId(id);
        if (pedido == null) {
            return false;
        }
        pedidoDAO.eliminar(id);
        return true;
    }

}
