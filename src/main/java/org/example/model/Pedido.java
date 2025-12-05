package org.example.model;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Relación con Cliente (muchos pedidos -> un cliente)
    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id" )
    private Cliente cliente;
    // Relación unidireccional: Pedido -> Producto
    @ManyToMany (fetch = FetchType.EAGER)
    @JoinColumn(name = "pedido_id") // crea la FK en la tabla de Producto
    private List<Producto> productos;
    private LocalDate fecha;
    private Double total;
}
