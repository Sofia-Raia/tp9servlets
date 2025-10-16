package org.example.mapper;

import org.example.dto.ClienteDTO;
import org.example.dto.PedidoDTO;
import org.example.dto.ProductoDTO;
import org.example.model.Cliente;
import org.example.model.Pedido;
import org.example.model.Producto;
import org.example.util.FuncionApp;

import java.util.stream.Collectors;
//convierte las entidades JPA a DTOs
public class MapperUtil {
    public static ProductoDTO toProductoDTO(Producto producto) {
        return ProductoDTO.builder()
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .build();
    }
    public static PedidoDTO toPedidoDTO(Pedido pedido) {
        return PedidoDTO.builder()
                .clienteNombre(pedido.getCliente().getNombre())
                .productos(pedido.getProductos().stream()
                        .map(MapperUtil::toProductoDTO)
                        .collect(Collectors.toList()))
                .total(pedido.getTotal())
                .fechaPedido(FuncionApp.getFechaString(pedido.getFecha()))
                .build();
    }
    public static ClienteDTO toClienteDTO(Cliente cliente) {
        return ClienteDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .pedidos(cliente.getPedidos().stream().
                        map(MapperUtil::toPedidoDTO)
                .collect(Collectors.toList()))
                .build();
    }

}

