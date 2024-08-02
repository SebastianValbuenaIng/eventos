package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "boleta")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BoletaEntity {
    @Id
    private Integer id;
    private Integer valor;
    private Integer cantidad_boletas;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private RolEntity rol;

    @ManyToOne
    @JoinColumn(name = "id_evento", nullable = false)
    private EventoEntity evento;
}
