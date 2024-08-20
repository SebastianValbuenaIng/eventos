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
public class AdminCompraResponse {
    private Integer id;
    private String nombre;
    private LocalDateTime fecha_pago;
    private Integer valor;
    private String estado_pago;
    private String rol;
}
