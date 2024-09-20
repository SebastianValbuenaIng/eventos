package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services;

public interface PagarService {
    void generatePay(String descripcion, String valor, String estado_pol);
    void enviarCorreoPrueba();
}
