package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.BoletaCompraEntity;

import java.util.List;
import java.util.Map;

public interface PagarService {
    Map<String, Long> pagar(String documento, Integer valor);
    void generatePay(String descripcion, String valor, String estado_pol);
}
