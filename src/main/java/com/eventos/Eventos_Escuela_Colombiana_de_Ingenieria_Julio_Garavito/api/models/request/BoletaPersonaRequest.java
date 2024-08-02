package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoletaPersonaRequest {
    private String correo;
    private String telefono;
    private String nombre;
    private String documento;
    private String rol;
    private String correo_facturacion;
    private Integer id_boleta;
    private Integer cantidad_boletas;
}
