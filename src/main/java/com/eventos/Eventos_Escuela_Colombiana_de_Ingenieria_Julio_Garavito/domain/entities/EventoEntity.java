package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "evento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoEntity {
    @Id
    private Integer id;
    private String nombre;
    @Column(length = 1)
    private String estado;
    private LocalDate fecha_inicio_preventa;
    private LocalDate fecha_final_preventa;
    private LocalDate fecha_inicio_regular;
    private LocalDate fecha_final_regular;
}
