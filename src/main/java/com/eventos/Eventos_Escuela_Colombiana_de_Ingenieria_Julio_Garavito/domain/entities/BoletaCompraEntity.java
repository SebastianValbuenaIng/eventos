package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

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

    @Column(name = "consecutivo_boleta", insertable = false, updatable = false)
    @Generated(GenerationTime.ALWAYS)
    private Integer consecutivoBoleta;

    private Integer valor;

    @ManyToOne
    @JoinColumn(name = "nro_compra", nullable = true)
    private CompraEntity compra;

    @ManyToOne
    @JoinColumn(name = "id_boleta", nullable = false)
    private BoletaEntity boleta;
}
