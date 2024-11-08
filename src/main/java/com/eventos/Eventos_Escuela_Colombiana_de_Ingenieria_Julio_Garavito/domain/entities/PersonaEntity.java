package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String correo;
    private String telefono;
    private String nombre;
    private String documento;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = true)
    private RolEntity rol;

    @ManyToOne
    @JoinColumn(name = "id_evento")
    private EventoEntity evento;
}
