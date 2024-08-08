package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.CompraEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.PersonaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompraRepository extends CrudRepository<CompraEntity, Integer> {
    List<CompraEntity> findAllByPersona(PersonaEntity persona);
}
