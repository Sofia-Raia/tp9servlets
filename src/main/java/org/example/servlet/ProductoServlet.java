package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.ProductoDAO;
import org.example.dto.ProductoDTO;
import org.example.mapper.MapperUtil;
import org.example.model.Producto;
import org.example.service.ProductoService;

import java.io.IOException;
import java.util.List;

@WebServlet("/productos/*")
public class ProductoServlet extends HttpServlet {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    // =========================================================
    // GET (Buscar todos o por ID)
    // =========================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        ProductoService productoService = new ProductoService(productoDAO);
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // ---- Listar todos ----
                List<ProductoDTO> productosDTO = productoService.listarProductos();
                mapper.writeValue(resp.getWriter(), productosDTO);
            } else {
                // ---- Buscar por ID ----
                Long id = Long.parseLong(pathInfo.substring(1));
                ProductoDTO productoDTO = productoService.buscarProductoPorId(id);
                mapper.writeValue(resp.getWriter(), productoDTO);
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID inválido. El ID debe ser un número.\"}"
            );
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // =========================================================
    // POST (Crear)
    // =========================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        try {
            ProductoDTO productoDTO = mapper.readValue(req.getInputStream(), ProductoDTO.class);
            ProductoService productoService = new ProductoService(productoDAO);

            Producto producto = productoService.crearProductoDesdeDTO(productoDTO);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), MapperUtil.toProductoDTO(producto));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error al crear producto: Revise el JSON enviado.\"}");
        }
    }

    // =========================================================
    // PUT (Actualizar)
    // =========================================================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Debe especificar el ID del producto a actualizar.\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            ProductoDTO productoDTO = mapper.readValue(req.getInputStream(), ProductoDTO.class);

            ProductoService productoService = new ProductoService(productoDAO);
            ProductoDTO productoActualizado = productoService.actualizarProducto(id, productoDTO);

            if (productoActualizado == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Producto no encontrado para actualizar.\"}");
                return;
            }

            mapper.writeValue(resp.getWriter(), productoActualizado);

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID inválido. El ID debe ser un número.\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Error inesperado al actualizar: " + e.getMessage() + "\"}");
        }
    }

    // =========================================================
    // DELETE (Eliminar)
    // =========================================================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Debe especificar el ID del producto a eliminar.\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));

            ProductoService productoService = new ProductoService(productoDAO);
            boolean eliminado = productoService.eliminarProducto(id);

            if (eliminado) {
                resp.getWriter().write("{\"mensaje\":\"Producto con ID " + id + " eliminado con éxito.\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Producto no encontrado para eliminar.\"}");
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID inválido. El ID debe ser un número.\"}");
        }
    }
}