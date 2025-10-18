package org.example.service;

import org.example.dao.ClienteDAO;
import org.example.dao.PedidoDAO;
import org.example.dto.ClienteDTO;
import org.example.mapper.MapperUtil;
import org.example.model.Cliente;
import org.example.model.Pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteService {
    private final ClienteDAO clienteDAO;
    private final PedidoDAO pedidoDAO;

    // Constructor completo para operaciones que requieran ambos DAOs (ej: PUT)
    public ClienteService(ClienteDAO clienteDAO, PedidoDAO pedidoDAO) {
        this.clienteDAO = clienteDAO;
        this.pedidoDAO = pedidoDAO;
    }

    // Constructor simplificado para operaciones que solo requieran ClienteDAO (ej: GET, POST, DELETE)
    public ClienteService(ClienteDAO clienteDAO) {
        this(clienteDAO, null);
    }

    // Listar todos los clientes
    public List<ClienteDTO> listarClientes() {
        List<Cliente> clientes = clienteDAO.listar();
        return clientes.stream()
                .map(MapperUtil::toClienteDTO)
                .collect(Collectors.toList());
    }

    // Buscar un cliente por su ID
    public ClienteDTO buscarClientePorId(Long id) throws Exception {
        Cliente cliente = clienteDAO.buscarPorId(id);
        if (cliente == null) {
            throw new Exception("Cliente no encontrado");
        }
        return MapperUtil.toClienteDTO(cliente);
    }

    // Crear un nuevo cliente
    public Cliente crearClienteDesdeDTO(ClienteDTO clienteDTO) {
        Cliente cliente = Cliente.builder()
                .nombre(clienteDTO.getNombre())
                .email(clienteDTO.getEmail())
                .telefono(clienteDTO.getTelefono())
                .pedidos(new ArrayList<>()) // Se inicializa sin pedidos al crear
                .build();

        clienteDAO.guardar(cliente);
        return cliente;
    }

    // Actualizar un cliente
    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO) {
        Cliente clienteExistente = clienteDAO.buscarPorId(id);
        if (clienteExistente == null) {
            return null; // El Servlet manejará el error 404
        }

        // 1. Actualizar campos simples
        clienteExistente.setNombre(clienteDTO.getNombre());
        clienteExistente.setTelefono(clienteDTO.getTelefono());
        clienteExistente.setEmail(clienteDTO.getEmail());

        // 2. Actualizar la lista de pedidos (requiere PedidoDAO)
        // Se asume que los PedidoDTO solo tienen el ID para buscarlos en la base de datos
        if (pedidoDAO != null && clienteDTO.getPedidos() != null) {
            List<Pedido> pedidosActualizados = clienteDTO.getPedidos().stream()
                    .map(pedidoDTO -> pedidoDAO.buscarPorId(pedidoDTO.getId()))
                    .filter(pedido -> pedido != null) // Ignora IDs de pedidos no existentes
                    .collect(Collectors.toList());
            clienteExistente.setPedidos(pedidosActualizados);
        }

        clienteDAO.actualizar(clienteExistente);
        return MapperUtil.toClienteDTO(clienteExistente);
    }

    // Eliminar un cliente por su ID
    public boolean eliminarCliente(Long id) {
        Cliente cliente = clienteDAO.buscarPorId(id);
        if (cliente == null) {
            return false;
        }
        clienteDAO.eliminar(id);
        return true;
    }
}