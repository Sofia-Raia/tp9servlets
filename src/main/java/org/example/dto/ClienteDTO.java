package org.example.dto;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.example.model.Pedido;

import java.util.List;

/*
 * DTO (Data Transfer Object) que representa la vista externa de un Cliente.
 *
 * Esta clase se utiliza para transferir datos de Clientes entre las diferentes
 * capas de la aplicación (por ejemplo, desde la capa de servicio hacia la capa
 * de presentación o como respuesta de una API REST).
 *
 * Su propósito principal es:
 * 1. Desacoplar el modelo interno (Entidad JPA 'Cliente') del modelo expuesto
 * al exterior.
 * 2. Evitar exponer detalles de la persistencia (como anotaciones de JPA)
 * o datos sensibles.
 * 3. Modelar la estructura exacta de datos que el "cliente" (frontend o
 * consumidor de la API) necesita recibir.*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {

    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private List<PedidoDTO>pedidos;
}
