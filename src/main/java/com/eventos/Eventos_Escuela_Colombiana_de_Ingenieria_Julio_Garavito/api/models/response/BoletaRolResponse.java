package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoletaRolResponse {
    private Integer id_boleta_principal;
    private Integer id_boleta_invitado;
    private Map<String, Integer> precios_boletas;
    private LocalDate fecha_venta_fin;
}
