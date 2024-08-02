package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.PersonaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PersonaRepository extends CrudRepository<PersonaEntity, Integer> {
    Optional<PersonaEntity> findByDocumento(String documento);
}
