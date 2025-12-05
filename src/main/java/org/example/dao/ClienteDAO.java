package org.example.dao;

import jakarta.persistence.EntityManager;
import org.example.model.Cliente;
import org.example.model.Pedido;

import java.util.List;
/**
* Implementación del patrón Data Access Object (DAO) para la entidad Cliente
* Esta clase centraliza y encapsula toda la lógica de acceso y persistencia
* de datos (CRUD) relacionada con los objetos Cliente.
 */
public class ClienteDAO {

    public void guardar(Cliente cliente) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(cliente);
        em.getTransaction().commit();
        em.close();
    }

    public Cliente buscarPorId(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        Cliente c = em.find(Cliente.class, id);
        em.close();
        return c;
    }

    public List<Cliente> listar() {
        EntityManager em = JpaUtil.getEntityManager();
        List<Cliente> clientes = em.createQuery("from Cliente", Cliente.class).getResultList();
        em.close();
        return clientes;
    }
    // Nuevo método para buscar por nombre
    public Cliente buscarPorNombre(String nombre) {
        return listar().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }
    public void eliminar(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        Cliente cliente = em.find(Cliente.class, id);
        if (cliente != null) {
            em.remove(cliente);
        }
        em.getTransaction().commit();
        em.close();
    }

    public void actualizar(Cliente cliente) {
        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(cliente);
        em.getTransaction().commit();
        em.close();
    }

}
