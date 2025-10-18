package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.example.model.Producto;
import java.util.List;
import java.util.stream.Collectors;

public class ProductoDAO {

    public void guardar(Producto producto) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(producto);
        em.getTransaction().commit();
        em.close();
    }

    public Producto buscarPorId(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        Producto p = em.find(Producto.class, id);
        em.close();
        return p;
    }

    public List<Producto> listar() {
        EntityManager em = JpaUtil.getEntityManager();
        List<Producto> productos = em.createQuery("from Producto", Producto.class).getResultList();
        em.close();
        return productos;
    }
    /**
     * Busca un producto por su nombre utilizando una consulta JPQL directa.
     * Esta es la forma óptima de evitar cargar toda la tabla en memoria.
     * @param nombre El nombre del producto a buscar.
     * @return El Producto encontrado o null si no existe.
     */
    public Producto buscarPorNombre(String nombre) {
        EntityManager em = JpaUtil.getEntityManager();
        Producto producto = null;

        try {
            // JPQL: Usamos 'LOWER' para buscar ignorando mayúsculas/minúsculas,
            // alineado con tu lógica original.
            String jpql = "SELECT p FROM Producto p WHERE LOWER(p.nombre) = :nombreProducto";

            TypedQuery<Producto> query = em.createQuery(jpql, Producto.class);
            query.setParameter("nombreProducto", nombre.toLowerCase()); // Convertimos el nombre de búsqueda a minúsculas

            // getSingleResult() lanza una excepción si no hay resultados,
            // por eso lo envolvemos en un try-catch.
            producto = query.getSingleResult();

        } catch (NoResultException e) {
            // Esto es normal si no se encuentra el producto. Retornará 'null'.
        } finally {
            em.close();
        }

        return producto;
    }
    // ... (Métodos existentes: guardar, buscarPorId, listar, eliminar, y el buscarPorNombre simple)

    // =========================================================
    // Nuevo método: Buscar productos por un arreglo/lista de nombres
    // =========================================================
    /**
     * Busca una lista de productos cuyos nombres coinciden con la lista proporcionada.
     * Utiliza el operador JPQL 'IN' para una consulta eficiente.
     * * @param nombres Lista de nombres de productos a buscar.
     * @return Una lista de Productos encontrados.
     */
    public List<Producto> buscarProductosPorNombres(List<String> nombres) {
        EntityManager em = JpaUtil.getEntityManager();
        List<Producto> productos = null;

        try {
            // 1. Convertir la lista de nombres a minúsculas
            // Esto asegura que la búsqueda con LOWER() en la BD funcione correctamente.
            List<String> nombresEnMinusculas = nombres.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            // JPQL: Usamos el operador 'IN' junto con la función 'LOWER' para buscar
            // sin distinguir entre mayúsculas y minúsculas y de manera eficiente.
            String jpql = "SELECT p FROM Producto p WHERE LOWER(p.nombre) IN :listaDeNombres";

            TypedQuery<Producto> query = em.createQuery(jpql, Producto.class);
            query.setParameter("listaDeNombres", nombresEnMinusculas);

            productos = query.getResultList();

        } catch (Exception e) {
            // Manejo de error general (puedes loguear la excepción aquí)
            e.printStackTrace();
        } finally {
            em.close();
        }

        return productos;
    }
    public void eliminar(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();

        // 1. Buscar la entidad para asegurarnos de que existe y está gestionada
        Producto producto = em.find(Producto.class, id);

        if (producto != null) {
            // 2. Eliminar la entidad
            em.remove(producto);
        }

        em.getTransaction().commit();
        em.close();
    }
}