package org.example.dao;

import jakarta.persistence.EntityManager;
import org.example.model.Pedido;
import java.util.List;

public class PedidoDAO {

    public void guardar(Pedido pedido) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(pedido);
        em.getTransaction().commit();
        em.close();
    }

    public Pedido buscarPorId(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        Pedido p = em.find(Pedido.class, id);
        em.close();
        return p;
    }

    public List<Pedido> listar() {
        EntityManager em = JpaUtil.getEntityManager();
        List<Pedido> pedidos = em.createQuery("from Pedido", Pedido.class).getResultList();
        em.close();
        return pedidos;
    }
    public void eliminar(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        Pedido pedido = em.find(Pedido.class, id);
        if (pedido != null) {
            em.remove(pedido);
        }
        em.getTransaction().commit();
        em.close();
    }

    public void actualizar(Pedido pedido) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(pedido);
        em.getTransaction().commit();
        em.close();
    }

}
