package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.administracion_empresas;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.request.administracion_empresas.ParticipanteRequest;

import java.util.Map;

public interface ParticipanteAdminEmpresasService {
    Map<String, String> inscripcionPersona(ParticipanteRequest participanteRequest);
    byte[] generateReport(String correo);
}
