package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminBoletaPagadaResponse {
    private String nombre;
    private Integer consecutivo_boleta;
    private LocalDateTime fecha_pago;
    private String rol;
    private String correo;
    private String telefono;
    private Integer valor;
}
