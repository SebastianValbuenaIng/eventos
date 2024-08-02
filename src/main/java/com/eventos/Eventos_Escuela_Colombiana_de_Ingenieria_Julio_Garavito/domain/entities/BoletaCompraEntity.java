package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "boleta_compra")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BoletaCompraEntity {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "nro_compra", nullable = false)
    private CompraEntity compra;

    @ManyToOne
    @JoinColumn(name = "id_boleta", nullable = false)
    private BoletaEntity boleta;
}
