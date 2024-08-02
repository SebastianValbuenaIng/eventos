package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.request.BoletaPersonaRequest;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.BoletaRolResponse;

import java.util.Map;

public interface BoletaService {
    BoletaRolResponse getBoletasByRol(String rol);
    Map<String, String> agregarAlCarrito(BoletaPersonaRequest boletaPersonaRequest);
    Map<String, String> eliminarDelCarrito(Integer id_carrito_persona);
    void changeValuesBoletaByDayFechaInicioRegular();
}
