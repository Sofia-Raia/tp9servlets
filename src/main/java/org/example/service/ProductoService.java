package org.example.service;

import org.example.dao.ProductoDAO;
import org.example.dto.ProductoDTO;
import org.example.mapper.MapperUtil;
import org.example.model.Producto;

import java.util.List;
import java.util.stream.Collectors;

public class ProductoService {

    private final ProductoDAO productoDAO;

    // Inyección de dependencia a través del constructor
    public ProductoService(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    /**
     * Lista todos los productos disponibles.
     * @return Una lista de ProductoDTO.
     */
    public List<ProductoDTO> listarProductos() {
        List<Producto> productos = productoDAO.listar();
        return productos.stream()
                .map(MapperUtil::toProductoDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca un producto por su ID.
     * @param id El ID del producto a buscar.
     * @return El ProductoDTO encontrado.
     * @throws Exception si el producto no existe.
     */
    public ProductoDTO buscarProductoPorId(Long id) throws Exception {
        Producto producto = productoDAO.buscarPorId(id);
        if (producto == null) {
            throw new Exception("Producto no encontrado con ID: " + id);
        }
        return MapperUtil.toProductoDTO(producto);
    }

    /**
     * Crea un nuevo producto a partir de un DTO.
     * @param productoDTO El DTO con la información del nuevo producto.
     * @return El objeto Producto creado.
     */
    public Producto crearProductoDesdeDTO(ProductoDTO productoDTO) {
        // En tu modelo, Producto no tiene stock, así que solo usamos nombre y precio
        Producto producto = Producto.builder()
                .nombre(productoDTO.getNombre())
                // La categoría no está en el DTO, puedes agregarla o dejarla null si es opcional
                .precio(productoDTO.getPrecio())
                .build();

        productoDAO.guardar(producto);
        return producto;
    }

    /**
     * Actualiza un producto existente.
     * @param id El ID del producto a actualizar.
     * @param productoDTO El DTO con los datos actualizados.
     * @return El ProductoDTO actualizado, o null si el producto no se encuentra.
     */
    public ProductoDTO actualizarProducto(Long id, ProductoDTO productoDTO) {
        Producto productoExistente = productoDAO.buscarPorId(id);
        if (productoExistente == null) {
            return null; // El Servlet manejará el error 404
        }

        // Actualizar campos permitidos
        productoExistente.setNombre(productoDTO.getNombre());
        productoExistente.setPrecio(productoDTO.getPrecio());
        // Nota: La categoría se ignora ya que no está en ProductoDTO

        productoDAO.guardar(productoExistente); // Asume que 'guardar' maneja persist y merge
        return MapperUtil.toProductoDTO(productoExistente);
    }

    /**
     * Elimina un producto por su ID.
     * @param id El ID del producto a eliminar.
     * @return true si fue eliminado, false si no se encontró.
     */
    public boolean eliminarProducto(Long id) {
        Producto producto = productoDAO.buscarPorId(id);
        if (producto == null) {
            return false;
        }
        productoDAO.eliminar(id);
        return true;
    }
}