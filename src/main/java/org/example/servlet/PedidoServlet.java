package org.example.servlet;
//La capa servlet cumple el papel de controlador,es decir, recibe las peticiones HTTP
//es la “puerta de entrada” del backend.


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.dao.ClienteDAO;
import org.example.dao.PedidoDAO;
import org.example.dao.ProductoDAO;
import org.example.dto.PedidoDTO;
import org.example.mapper.MapperUtil;
import org.example.model.Cliente;
import org.example.model.Pedido;
import org.example.model.Producto;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/pedidos/*")
public class PedidoServlet extends HttpServlet {

    private PedidoDAO pedidoDAO = new PedidoDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private ProductoDAO productoDAO = new ProductoDAO();
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
            List<Pedido> pedidos = pedidoDAO.listar();
            List<PedidoDTO> pedidosDTO = pedidos.stream()
                    .map(MapperUtil::toPedidoDTO)
                    .collect(Collectors.toList());
            mapper.writeValue(resp.getWriter(), pedidosDTO);

        } else {
            // ---- Muestra por id ----
            try {
                Long id = Long.parseLong(pathInfo.substring(1));
                Pedido pedido = pedidoDAO.buscarPorId(id);

                if (pedido != null) {
                    PedidoDTO dto = MapperUtil.toPedidoDTO(pedido);
                    mapper.writeValue(resp.getWriter(), dto);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Pedido no encontrado\"}");
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
            PedidoDTO pedidoDTO = mapper.readValue(req.getInputStream(), PedidoDTO.class);

            // Buscamos cliente por nombre (podrías cambiar a buscar por id si querés)
            Cliente cliente = clienteDAO.listar().stream()
                    .filter(c -> c.getNombre().equalsIgnoreCase(pedidoDTO.getClienteNombre()))
                    .findFirst().orElse(null);

            if (cliente == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Cliente no encontrado\"}");
                return;
            }

            List<Producto> productos = pedidoDTO.getProductos().stream()
                    .map(dto -> productoDAO.listar().stream()
                            .filter(p -> p.getNombre().equalsIgnoreCase(dto.getNombre()))
                            .findFirst()
                            .orElse(null))
                    .filter(p -> p != null)
                    .collect(Collectors.toList());

            Pedido pedido = Pedido.builder()
                    .cliente(cliente)
                    .productos(productos)
                    .fecha(LocalDate.now())
                    .total(pedidoDTO.getTotal())
                    .build();

            pedidoDAO.guardar(pedido);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), MapperUtil.toPedidoDTO(pedido));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error al crear pedido\"}");
        }
    }

    // =========================================================
    // Actualiza el pedido existente
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
            Pedido pedidoExistente = pedidoDAO.buscarPorId(id);

            if (pedidoExistente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Pedido no encontrado\"}");
                return;
            }

            PedidoDTO pedidoDTO = mapper.readValue(req.getInputStream(), PedidoDTO.class);

            // Actualizar campos
            pedidoExistente.setTotal(pedidoDTO.getTotal());
            pedidoExistente.setFecha(LocalDate.now());

            pedidoDAO.guardar(pedidoExistente); // como usamos persist, puede reemplazarse por merge()

            mapper.writeValue(resp.getWriter(), MapperUtil.toPedidoDTO(pedidoExistente));

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
            Pedido pedido = pedidoDAO.buscarPorId(id);

            if (pedido != null) {
                pedidoDAO.eliminar(id);
                resp.getWriter().write("{\"mensaje\":\"Pedido eliminado\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Pedido no encontrado\"}");
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID inválido\"}");
        }
    }
}
