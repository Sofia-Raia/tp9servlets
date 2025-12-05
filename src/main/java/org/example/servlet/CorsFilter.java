package org.example.servlet;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * Filtro CORS (Cross-Origin Resource Sharing)
 *
 * Esta clase permite que el backend acepte peticiones HTTP desde otros orígenes
 * (por ejemplo, desde una aplicación frontend en otro puerto o dominio).
 *
 * Funcionalidad principal:
 *  - Agrega los encabezados CORS necesarios a todas las respuestas HTTP.
 *  - Autoriza métodos como GET, POST, PUT, DELETE y OPTIONS.
 *  - Maneja las peticiones preflight (método OPTIONS), respondiendo con 200 OK
 *    sin pasar la solicitud al resto de la aplicación.
 *  - Permite la comunicación entre el cliente (frontend) y el servidor (backend)
 *    evitando bloqueos por la política de mismo origen del navegador.
 *
 * Aplicado a todas las rutas mediante la anotación @WebFilter("/*").
 */


@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // --- 1. Establecer Encabezados CORS ---
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With, accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
        httpResponse.setHeader("Access-Control-Max-Age", "3600"); // Cache preflight response por 1 hora

        // --- 2. Manejar la Petición Preflight (OPTIONS) ---
        // Si el método es OPTIONS, respondemos inmediatamente y terminamos.
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK); // Devuelve 200 OK
            return; // Detiene el procesamiento, no pasa al Servlet
        }

        // --- 3. Continuar con la Cadena de Filtros para peticiones normales ---
        chain.doFilter(request, response);
    }

    // Métodos init y destroy
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
