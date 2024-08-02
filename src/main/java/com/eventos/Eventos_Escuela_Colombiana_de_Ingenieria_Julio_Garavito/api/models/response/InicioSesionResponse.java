package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InicioSesionResponse {
    private String area;
    private String nombre;
    private String rol;
    private String direccion;
    private String correo;
    private String nro_documento;
    private String telefono;
}
