package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.InicioSesionResponse;

public interface IniciarSesionService {
    InicioSesionResponse iniciarSesion(String correo);
    InicioSesionResponse iniciarSesionGraduado(String nro_documento);
}
