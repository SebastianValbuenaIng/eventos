package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.administracion_empresas;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.administracion_empresas.GraduadoAdminEmpresasResponse;

import java.util.Map;

public interface IniciarSesionAdminEmpresasService {
    GraduadoAdminEmpresasResponse iniciarSesionAdminEmpresas(String documento);
    Map<String, String> iniciarSesionAdmin(String correo);
}
