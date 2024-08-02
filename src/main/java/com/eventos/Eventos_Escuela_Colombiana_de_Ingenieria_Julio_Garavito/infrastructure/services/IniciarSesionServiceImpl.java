package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.services;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.InicioSesionResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.IniciarSesionService;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.IdNotFoundException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.ServerErrorException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.helpers.AppMovilConnection;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.helpers.RegistroConnection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Map;

@Service
@AllArgsConstructor
public class IniciarSesionServiceImpl implements IniciarSesionService {
    private final AppMovilConnection appMovilConnection;
    private final RegistroConnection registroConnection;

    @Override
    public InicioSesionResponse iniciarSesion(String correo) {
        try {
            String consulta = String.format("SELECT * FROM dbo.personas_activas WHERE email = '%s'", correo);

            Map<String, Object> returnQuery = appMovilConnection.executeSelectSql(consulta);

            if (returnQuery.isEmpty()) throw new IdNotFoundException("app_movil");

            return InicioSesionResponse
                    .builder()
                    .area(String.valueOf(returnQuery.get("area")).trim())
                    .correo(String.valueOf(returnQuery.get("email")))
                    .rol(String.valueOf(returnQuery.get("tipo_rol")))
                    .direccion(String.valueOf(returnQuery.get("direccion")))
                    .nro_documento(String.valueOf(returnQuery.get("nro_documento")))
                    .telefono(String.valueOf(returnQuery.get("telefono")))
                    .nombre(String.valueOf(returnQuery.get("nombre")))
                    .build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new ServerErrorException();
        }
    }

    @Override
    public InicioSesionResponse iniciarSesionGraduado(String nro_documento) {
        try {
            String consulta = String.format("""
                select distinct b.national_id, b.nombre, a.documento, a.email, a.tel_residencia, a.cel
                from uge.migracion_directa_graduados b
                inner join uge.datos_egre a on (b.NATIONAL_ID = a.documento) where documento = '%s'""", nro_documento);

            Map<String, Object> returnQuery = registroConnection.executeSelectSql(consulta);

            if (returnQuery.isEmpty()) throw new IdNotFoundException("registro_graduado");

            return InicioSesionResponse
                    .builder()
                    .area(null)
                    .correo(String.valueOf(returnQuery.get("email")))
                    .rol("Graduado")
                    .direccion(null)
                    .nro_documento(String.valueOf(returnQuery.get("documento")))
                    .telefono(String.valueOf(returnQuery.get("tel_residencia")))
                    .nombre(String.valueOf(returnQuery.get("nombre")))
                    .build();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new ServerErrorException();
        }
    }
}
