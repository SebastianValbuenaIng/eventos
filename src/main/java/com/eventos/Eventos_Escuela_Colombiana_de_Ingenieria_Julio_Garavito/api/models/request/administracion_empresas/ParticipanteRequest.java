package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.request.administracion_empresas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteRequest {
    private String nombre;
    private String documento;
    private Integer year_grado;
    private String telefono;
    private String email;
    private String empresa;
    private String cargo;
    private String restriccion_alimentos;
    private Boolean asistencia_evento;
}
