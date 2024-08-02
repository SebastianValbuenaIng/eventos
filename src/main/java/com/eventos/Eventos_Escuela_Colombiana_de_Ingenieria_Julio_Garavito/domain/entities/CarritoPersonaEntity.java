package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "carrito_persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoPersonaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDateTime fecha_reserva;

    @ManyToOne
    @JoinColumn(name = "id_persona", nullable = false)
    private PersonaEntity persona;

    @ManyToOne
    @JoinColumn(name = "id_boleta", nullable = false)
    private BoletaEntity boleta;
}
