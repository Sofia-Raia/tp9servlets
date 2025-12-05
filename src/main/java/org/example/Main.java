package org.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.FilterHolder;
import org.example.servlet.ClienteServlet;
import org.example.servlet.PedidoServlet;
import org.example.servlet.ProductoServlet;
import org.example.servlet.CorsFilter;
import java.util.EnumSet;
import jakarta.servlet.DispatcherType;

public class Main {

    public static void main(String[] args) throws Exception {

        // Crear servidor en el puerto 8080
        Server server = new Server(8080);

        // Crear contexto de servlets
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/tienda-lombok-dto"); // Configura el contexto base de la URL (Importante!)
        server.setHandler(context);


        // 1. REGISTRAR FILTRO CORS
        FilterHolder corsFilterHolder = new FilterHolder(new CorsFilter());

        // El filtro debe aplicarse a todas las peticiones (/*) y a todos los tipos de envío (REQUEST, OPTIONS, etc.)
        context.addFilter(corsFilterHolder,
                "/*",
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR));

        // 2. REGISTRAR SERVLETS
        context.addServlet(new ServletHolder(new PedidoServlet()), "/pedidos/*");
        context.addServlet(new ServletHolder(new ClienteServlet()), "/clientes/*");
        context.addServlet(new ServletHolder(new ProductoServlet()), "/productos/*");

        // Arrancar servidor
        try {
            server.start();
            // La URL ahora incluye el Context Path
            System.out.println("Servidor arrancado en http://localhost:8080/tienda-lombok-dto");
            server.join(); // mantener el servidor en ejecución
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }
}