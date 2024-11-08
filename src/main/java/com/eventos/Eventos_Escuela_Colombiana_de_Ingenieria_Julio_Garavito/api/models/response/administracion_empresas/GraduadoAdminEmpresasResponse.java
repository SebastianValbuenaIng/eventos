package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.administracion_empresas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GraduadoAdminEmpresasResponse {
    private String nombre;
    private Integer year_grado;
    private String documento;
    private String telefono;
    private String email;
}
