package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "informacion_adicional_persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InformacionAdicionalPersonaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String empresa_actual;
    private String cargo_actual;
    private Integer anio_grado;
    private String restriccion_alimentos;
    @Column(name = "documento_persona")
    private String documentoPersona;
    private String asistencia_evento;
}
