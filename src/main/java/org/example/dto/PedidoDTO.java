package org.example.dto;
import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//no se mapean con JPA
public class PedidoDTO {//no hacen fatal el @toString ni @EqualsAndHashCode
    private Long id;
    private String clienteNombre;
    private List<ProductoDTO> productos;
    private String fechaPedido;
    private Double total;
}
