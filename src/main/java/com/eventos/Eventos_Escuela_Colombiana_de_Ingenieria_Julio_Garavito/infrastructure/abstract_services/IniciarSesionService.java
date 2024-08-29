package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.InicioSesionResponse;

import java.util.Map;

public interface IniciarSesionService {
    Object iniciarSesion(String correo);
    InicioSesionResponse iniciarSesionGraduado(String nro_documento);
    Map<String, String> insertDatosGraduado(String correo, String telefono, String documento);
}
