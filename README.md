# #🛒 Tienda Online – Backend Lombok + DTO & Frontend TypeScript # #
📌 Descripción del Proyecto

Este trabajo práctico consiste en el desarrollo de una aplicación cliente-servidor para la gestión de pedidos de una tienda online.
El backend está implementado en Java, utilizando Lombok y el patrón DTO, mientras que el frontend está construido en TypeScript, consumiendo los datos expuestos por el backend mediante una API REST.

🎯 Objetivo General

Construir una aplicación completa que permita visualizar y gestionar pedidos, integrando tecnologías modernas tanto en backend como en frontend.

✅ Objetivos Específicos

Aplicar Lombok para reducir código boilerplate en entidades Java.

Implementar el patrón DTO para desacoplar modelo y presentación.

Exponer datos mediante una API REST utilizando Servlets.

Desarrollar un front simple en TypeScript, realizando peticiones HTTP (GET y POST).

Mostrar datos transformados por los DTOs en el navegador.

(Objetivo Anexo) Integrar persistencia con JPA para acceso a datos.

🧩 Parte 1: Backend con Lombok y DTO
📁 Proyecto Maven

📦 Paquetes

dao         → ClienteDAO, PedidoDAO, ProductoDAO y JpaUtil
dto         → ClienteDTO, ProductoDTO y PedidoDTO
mapper      → MapperUtil
model       → Producto, Cliente y Pedido
service     → ProductoService, ClienteService y PedidoService
servlet     → PedidoServlet, ProductoServlet, ClienteServlet y CorsFilter.
util        → FuncionApp


🧩 Parte 2: Frontend en TypeScript

link al repositorio del front : https://github.com/Sofia-Raia/tp9Front

📁 Estructura esperada
frontend/
│ index.html
│ styles.css
│ app.ts
│ tsconfig.json

Autora_ Sofia Raia.