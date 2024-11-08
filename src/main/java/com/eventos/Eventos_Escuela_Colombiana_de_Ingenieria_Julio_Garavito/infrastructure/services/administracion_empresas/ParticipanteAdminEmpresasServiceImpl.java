package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.services.administracion_empresas;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.request.administracion_empresas.ParticipanteRequest;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.EventoEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.InformacionAdicionalPersonaEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.PersonaEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.RolEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.*;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.administracion_empresas.ParticipanteAdminEmpresasService;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.IdNotFoundException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.MessageBadRequestException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.reports.administracion_empresas.ReportParticipantesAdminEmpresas;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ParticipanteAdminEmpresasServiceImpl implements ParticipanteAdminEmpresasService {
    private final InformacionAdicionalPersonaRepository informacionAdicionalPersonaRepository;
    private final PersonaRepository personaRepository;
    private final EventoRepository eventoRepository;
    private final RolRepository rolRepository;
    private final AdministradorRepository administradorRepository;
    private final ReportParticipantesAdminEmpresas reportParticipantesAdminEmpresas;

    @Override
    public Map<String, String> inscripcionPersona(ParticipanteRequest participanteRequest) {
        validNull(participanteRequest.getNombre(), "nombre");
        validNull(participanteRequest.getDocumento(), "documento");
        validNull(participanteRequest.getYear_grado(), "year_grado");
        validNull(participanteRequest.getTelefono(), "telefono");
        validNull(participanteRequest.getEmail(), "email");
        validNull(participanteRequest.getAsistencia_evento(), "asistencia_evento");

        validEmpty(participanteRequest.getNombre(), "nombre");
        validEmpty(participanteRequest.getDocumento(), "documento");
        validEmpty(participanteRequest.getTelefono(), "telefono");
        validEmpty(participanteRequest.getEmail(), "email");

        EventoEntity evento = eventoRepository.findByNombre("Evento Administración de Empresas")
                .orElseThrow(() -> new IdNotFoundException("evento"));

        Optional<PersonaEntity> persona = personaRepository
                .findByDocumentoAndEvento(participanteRequest.getDocumento(), evento);

        RolEntity rolGraduadoFound = rolRepository
                .findByDescripcion("Graduado")
                .orElseThrow(() -> new IdNotFoundException("rol"));

        if (persona.isPresent()) {
            PersonaEntity personaExist = persona.get();
            personaExist.setCorreo(participanteRequest.getEmail());
            personaExist.setTelefono(participanteRequest.getTelefono());

            personaRepository.save(personaExist);

            Optional<InformacionAdicionalPersonaEntity> informacionAdicionalPersona = informacionAdicionalPersonaRepository
                    .findByDocumentoPersona(participanteRequest.getDocumento());

            informacionAdicionalPersona.ifPresent(informacionAdicionalPersonaRepository::delete);
        } else {
            PersonaEntity newPersona = PersonaEntity
                    .builder()
                    .correo(participanteRequest.getEmail())
                    .documento(participanteRequest.getDocumento())
                    .telefono(participanteRequest.getTelefono())
                    .nombre(participanteRequest.getNombre())
                    .evento(evento)
                    .rol(rolGraduadoFound)
                    .build();

            personaRepository.save(newPersona);
        }

        InformacionAdicionalPersonaEntity informacionAdicionalPersona = InformacionAdicionalPersonaEntity
                .builder()
                .anio_grado(participanteRequest.getYear_grado())
                .documentoPersona(participanteRequest.getDocumento())
                .cargo_actual(participanteRequest.getCargo())
                .empresa_actual(participanteRequest.getEmpresa())
                .restriccion_alimentos(participanteRequest.getRestriccion_alimentos())
                .asistencia_evento(participanteRequest.getAsistencia_evento() ? "1" : "0")
                .build();

        informacionAdicionalPersonaRepository.save(informacionAdicionalPersona);

        return Map.of("message", "Inscrito guardado correctamente");
    }

    @Override
    public byte[] generateReport(String correo) {
        EventoEntity evento = eventoRepository.findByNombre("Evento Administración de Empresas")
                .orElseThrow(() -> new IdNotFoundException("evento"));

        administradorRepository.findByCorreoAndEvento(correo, evento)
                .orElseThrow(() -> new IdNotFoundException("usuario_administrador"));

        List<Map<String, Object>> findParticipantes = personaRepository.getParticipantesAdministracionEmpresas();

        try {
            return reportParticipantesAdminEmpresas.generateReport(findParticipantes);
        } catch (IOException e) {
            throw new InternalError();
        }
    }

    private void validNull(Object requestObject, String campo) {
        if (requestObject == null)
            throw new MessageBadRequestException("El campo " + campo + " no puede ser nulo");
    }

    private void validEmpty(String requestString, String campo) {
        if (requestString.isEmpty())
            throw new MessageBadRequestException("El campo " + campo + " no puede estar vacío");
    }
}
