package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "compra")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CompraEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "numero_referencia")
    private Long numeroReferencia;
    private LocalDateTime fecha_creacion;
    private LocalDateTime fecha_pago;
    private Integer valor;

    @ManyToOne
    @JoinColumn(name = "id_persona", nullable = false)
    private PersonaEntity persona;

    @ManyToOne
    @JoinColumn(name = "id_estado_compra", nullable = false)
    private EstadoCompraEntity estadoCompra;
}
