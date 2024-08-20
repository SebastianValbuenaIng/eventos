package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.AdminBoletaPagadaResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.AdminCompraResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.CompraAdminResponse;

import java.util.List;

public interface AdministradorService {
    List<AdminBoletaPagadaResponse> getBoletasAdmin(String correo);
    List<AdminCompraResponse> getCompras(String correo);
    CompraAdminResponse getCompraPersona(String correo, Integer id);
}
