package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.InformacionAdicionalPersonaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InformacionAdicionalPersonaRepository extends CrudRepository<InformacionAdicionalPersonaEntity, Integer> {
    Optional<InformacionAdicionalPersonaEntity> findByDocumentoPersona(String documentoPersona);
}
