package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.EventoEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.PersonaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PersonaRepository extends CrudRepository<PersonaEntity, Integer> {
    Optional<PersonaEntity> findByDocumento(String documento);
    Optional<PersonaEntity> findByCorreo(String correo);
    Optional<PersonaEntity> findByDocumentoAndEvento(String documento, EventoEntity evento);

    @Query(nativeQuery = true, value = """
                SELECT p.documento,
                       p.nombre,
                       p.correo,
                       p.telefono,
                       iap.empresa_actual,
                       iap.cargo_actual,
                       iap.anio_grado,
                       iap.restriccion_alimentos,
                       IIF(iap.asistencia_evento = '1', N'Sí', N'No') as asistencia_evento
                FROM even.persona p
                         LEFT JOIN even.informacion_adicional_persona iap ON iap.documento_persona = p.documento
                         LEFT JOIN even.evento e ON e.id = p.id_evento
                WHERE e.nombre = N'Evento Administración de Empresas'
            """)
    List<Map<String, Object>> getParticipantesAdministracionEmpresas();
}
