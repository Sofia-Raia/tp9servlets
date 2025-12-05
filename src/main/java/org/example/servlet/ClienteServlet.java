package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.ClienteDAO;
import org.example.dao.PedidoDAO;
import org.example.dto.ClienteDTO;
import org.example.mapper.MapperUtil;
import org.example.model.Cliente;
import org.example.service.ClienteService;

import java.io.IOException;
import java.util.List;
/**
 * Servlet (Controlador) que maneja las peticiones HTTP para el recurso /clientes.
 *
 * Es la "puerta de entrada" de la API. Se encarga de:
 * - Recibir las peticiones CRUD (GET, POST, PUT, DELETE).
 * - Leer la URL para obtener IDs (ej: /clientes/1).
 * - Convertir el JSON (del request) a objetos ClienteDTO.
 * - Llamar a la capa de Servicio (ClienteService) para que haga el trabajo.
 * - Convertir la respuesta (DTOs o errores) de vuelta a JSON.
 * - Manejar los códigos de estado HTTP (200, 201, 404, etc.).
 */
@WebServlet("/clientes/*")
public class ClienteServlet extends HttpServlet {

    // Mantener las instancias DAO para pasarlas al Service.
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ObjectMapper mapper = new ObjectMapper();


    // GET (Buscar todos o por ID)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //envia un json y le pide que lo interprete con charset=UTF-8
        resp.setContentType("application/json;charset=UTF-8");

        // Usamos el constructor simplificado para GET
        ClienteService clienteService = new ClienteService(clienteDAO);
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // ---- Listar todos ----
                List<ClienteDTO> clientesDTO = clienteService.listarClientes();
                mapper.writeValue(resp.getWriter(), clientesDTO);
            } else {
                // ---- Buscar por ID ----
                Long id = Long.parseLong(pathInfo.substring(1));
                ClienteDTO clienteDTO = clienteService.buscarClientePorId(id);
                mapper.writeValue(resp.getWriter(), clienteDTO);
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID inválido\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }


    // POST (Crear)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        try {
            ClienteDTO clienteDTO = mapper.readValue(req.getInputStream(), ClienteDTO.class);
            ClienteService clienteService = new ClienteService(clienteDAO); // Constructor simplificado

            Cliente cliente = clienteService.crearClienteDesdeDTO(clienteDTO);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), MapperUtil.toClienteDTO(cliente));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error al crear el cliente: " + e.getMessage() + "\"}");
        }
    }

    // PUT (Actualizar)
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Debe especificar el ID\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            ClienteDTO clienteDTO = mapper.readValue(req.getInputStream(), ClienteDTO.class);

            // Usamos el constructor completo ya que PUT puede implicar actualizar la lista de pedidos
            ClienteService clienteService = new ClienteService(clienteDAO, pedidoDAO);
            ClienteDTO clienteActualizado = clienteService.actualizarCliente(id, clienteDTO);

            if (clienteActualizado == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Cliente no encontrado\"}");
                return;
            }

            mapper.writeValue(resp.getWriter(), clienteActualizado);

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID inválido\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Error inesperado al actualizar: " + e.getMessage() + "\"}");
        }
    }


    // DELETE (Eliminar)
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Debe especificar el ID\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));

            ClienteService clienteService = new ClienteService(clienteDAO); // Constructor simplificado
            boolean eliminado = clienteService.eliminarCliente(id);

            if (eliminado) {
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