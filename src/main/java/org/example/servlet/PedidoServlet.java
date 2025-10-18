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
import org.example.model.Pedido;
import org.example.service.PedidoService;
import java.io.IOException;
import java.util.List;


@WebServlet("/pedidos/*")
public class PedidoServlet extends HttpServlet {

    private PedidoDAO pedidoDAO = new PedidoDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private ProductoDAO productoDAO = new ProductoDAO();
    private PedidoService pedidoService = new PedidoService(clienteDAO,productoDAO,pedidoDAO);
    private ObjectMapper mapper = new ObjectMapper();

    //Busca todos o por id
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");



        String pathInfo = req.getPathInfo(); // ej: /1 o null

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // ---- Listar todos ----
                List<PedidoDTO> pedidosDTO = pedidoService.listarPedidos();
                mapper.writeValue(resp.getWriter(), pedidosDTO);
            } else {
                // ---- Buscar por ID ----
                Long id = Long.parseLong(pathInfo.substring(1));
                PedidoDTO pedidoDTO = pedidoService.buscarPedidoPorId(id);
                mapper.writeValue(resp.getWriter(), pedidoDTO);
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID inválido\"}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
   //Crea
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        try {
            PedidoDTO pedidoDTO = mapper.readValue(req.getInputStream(), PedidoDTO.class);

            PedidoService pedidoService = new PedidoService(clienteDAO, productoDAO, pedidoDAO);

            Pedido pedido = pedidoService.crearPedidoDesdeDTO(pedidoDTO);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), MapperUtil.toPedidoDTO(pedido));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

   //Actualiza
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
           PedidoDTO pedidoDTO = mapper.readValue(req.getInputStream(), PedidoDTO.class);

           PedidoDTO pedidoActualizado = pedidoService.actualizarPedido(id, pedidoDTO);

           if (pedidoActualizado == null) {
               resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
               resp.getWriter().write("{\"error\":\"Pedido no encontrado\"}");
               return;
           }

           mapper.writeValue(resp.getWriter(), pedidoActualizado);

       } catch (NumberFormatException e) {
           resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
           resp.getWriter().write("{\"error\":\"ID inválido\"}");
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }

    //Eliminar
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

            boolean eliminado = pedidoService.eliminarPedido(id);

            if (eliminado) {
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
