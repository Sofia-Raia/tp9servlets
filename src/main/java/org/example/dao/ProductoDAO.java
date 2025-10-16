package org.example.dao;

import jakarta.persistence.EntityManager;
import org.example.model.Producto;
import java.util.List;

public class ProductoDAO {

    public void guardar(Producto producto) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(producto);
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
}