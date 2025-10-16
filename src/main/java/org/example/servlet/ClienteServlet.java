package org.example.servlet;
//La capa servlet cumple el papel de controlador,es decir, recibe las peticiones HTTP
//es la “puerta de entrada” del backend.


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.ClienteDAO;
import org.example.dao.PedidoDAO;
import org.example.dao.ProductoDAO;
import org.example.dto.ClienteDTO;
import org.example.dto.PedidoDTO;
import org.example.mapper.MapperUtil;
import org.example.model.Cliente;
import org.example.model.Pedido;
import org.example.model.Producto;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/clientes/*")
public class ClienteServlet extends HttpServlet {

    private PedidoDAO pedidoDAO = new PedidoDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private ObjectMapper mapper = new ObjectMapper();

    // =========================================================
    // GET
    // =========================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        String pathInfo = req.getPathInfo(); // ej: /1 o null

        if (pathInfo == null || pathInfo.equals("/")) {
            // ---- Muestra todos ----
            List<Cliente> clientes = clienteDAO.listar();
            List<ClienteDTO> clientesDTO = clientes.stream()
                    .map(MapperUtil::toClienteDTO)
                    .collect(Collectors.toList());
            mapper.writeValue(resp.getWriter(), clientesDTO);

        } else {
            // ---- Muestra por id ----
            try {
                Long id = Long.parseLong(pathInfo.substring(1));
                Cliente cliente = clienteDAO.buscarPorId(id);

                if (cliente != null) {
                    ClienteDTO dto = MapperUtil.toClienteDTO(cliente);
                    mapper.writeValue(resp.getWriter(), dto);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Cliente no encontrado\"}");
                }

            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"ID inválido\"}");
            }
        }
    }

    // =========================================================
    // Crear un nuevo pedido
    // =========================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        try {
            ClienteDTO clienteDTO = mapper.readValue(req.getInputStream(), ClienteDTO.class);

            Cliente cliente = Cliente.builder()

                    .nombre(clienteDTO.getNombre())
                    .email(clienteDTO.getEmail())
                    .telefono(clienteDTO.getTelefono())
                    .pedidos(new ArrayList<>())
                    .build();

            clienteDAO.guardar(cliente);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), MapperUtil.toClienteDTO(cliente));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error al crear el cliente\"}");
        }
    }

    // =========================================================
    // Actualizar
    // =========================================================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        String pathInfo = req.getPathInfo(); // /id
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Debe especificar el ID\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            Cliente clienteExistente = clienteDAO.buscarPorId(id);

            if (clienteExistente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Cliente no encontrado\"}");
                return;
            }

            ClienteDTO cDTO = mapper.readValue(req.getInputStream(), ClienteDTO.class);

            // Actualizar campos
            clienteExistente.setNombre(cDTO.getNombre());
            clienteExistente.setTelefono(cDTO.getTelefono());
            clienteExistente.setEmail(cDTO.getEmail());
            clienteExistente.setPedidos(cDTO.getPedidos().stream()
                    .map(pedidoDTO -> pedidoDAO.buscarPorId(pedidoDTO.getId()))
                    .collect(Collectors.toList())
            );

            clienteDAO.actualizar(clienteExistente); // como usamos persist, puede reemplazarse por merge()

            mapper.writeValue(resp.getWriter(), MapperUtil.toClienteDTO(clienteExistente));

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID inválido\"}");
        }
    }

    // =========================================================
    // Borrar el pedido
    // =========================================================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        String pathInfo = req.getPathInfo(); // /id
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Debe especificar el ID\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            Cliente cliente = clienteDAO.buscarPorId(id);

            if (cliente != null) {
                clienteDAO.eliminar(id);
                resp.getWriter().write("{\"mensaje\":\"Cliente eliminado\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Cliente no encontrado\"}");
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID inválido\"}");
        }
    }
}
