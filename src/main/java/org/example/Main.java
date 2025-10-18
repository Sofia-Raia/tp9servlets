package org.example;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.example.servlet.ClienteServlet;
import org.example.servlet.PedidoServlet;
import org.example.servlet.ProductoServlet;

public class Main {

    public static void main(String[] args) throws Exception {

        // Crear servidor en el puerto 8080
        Server server = new Server(8080);

        // Crear contexto de servlets con soporte de sesiones
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/"); // raíz de la app
        server.setHandler(context);

        // Registrar servlets
        context.addServlet(new ServletHolder(new PedidoServlet()), "/pedidos/*");
        context.addServlet(new ServletHolder(new ClienteServlet()), "/clientes/*");
        context.addServlet(new ServletHolder(new ProductoServlet()), "/productos/*");

        // Arrancar servidor
        try {
            server.start();
            System.out.println("Servidor arrancado en http://localhost:8080");
            server.join(); // mantener el servidor en ejecución
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }
}
