package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.services.administracion_empresas;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.administracion_empresas.GraduadoAdminEmpresasResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.AdministradorEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.EventoEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.PersonaEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.AdministradorRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.EventoRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.PersonaRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.administracion_empresas.IniciarSesionAdminEmpresasService;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.IdNotFoundException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.ServerErrorException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.helpers.RegistroConnection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class IniciarSesionAdminEmpresasServiceImpl implements IniciarSesionAdminEmpresasService {
    private final RegistroConnection registroConnection;
    private final AdministradorRepository administradorRepository;
    private final EventoRepository eventoRepository;
    private final PersonaRepository personaRepository;

    @Override
    public GraduadoAdminEmpresasResponse iniciarSesionAdminEmpresas(String documento) {
        try {
            String consulta = String.format("""
                    select distinct b.NOMBRE, YEAR(b.FECHA_GRADO) as anio_grado, a.documento, a.email
                    from uge.migracion_directa_graduados b
                             inner join uge.datos_egre a on (b.NATIONAL_ID = a.documento)
                    WHERE b.ACAD_PROG = 'ADMI' AND a.documento = '%s'
                    """, documento);

            Map<String, Object> returnQuery = registroConnection.executeSelectSql(consulta);

            if (returnQuery.isEmpty()) throw new IdNotFoundException("registro_graduado");

            Optional<PersonaEntity> persona = personaRepository.findByDocumento(documento);

            return GraduadoAdminEmpresasResponse
                    .builder()
                    .documento(String.valueOf(returnQuery.get("documento")))
                    .email(persona.isPresent() ? persona.get().getCorreo() : String.valueOf(returnQuery.get("email")))
                    .telefono(persona.isPresent() ? persona.get().getTelefono() : "")
                    .nombre(String.valueOf(returnQuery.get("NOMBRE")))
                    .year_grado(Integer.valueOf(String.valueOf(returnQuery.get("anio_grado"))))
                    .build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new ServerErrorException();
        }
    }

    @Override
    public Map<String, String> iniciarSesionAdmin(String correo) {
        EventoEntity evento = eventoRepository.findByNombre("Evento Administración de Empresas")
                .orElseThrow(() -> new IdNotFoundException("evento"));

        AdministradorEntity administrador = administradorRepository.findByCorreoAndEvento(correo, evento)
                .orElseThrow(() -> new IdNotFoundException("administrador"));

        return Map.of("message", administrador.getCorreo());
    }
}
