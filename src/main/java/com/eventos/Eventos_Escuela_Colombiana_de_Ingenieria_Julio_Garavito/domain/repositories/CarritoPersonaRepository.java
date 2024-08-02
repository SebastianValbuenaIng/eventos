package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.BoletaEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.CarritoPersonaEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.PersonaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CarritoPersonaRepository extends CrudRepository<CarritoPersonaEntity, Integer> {
    List<CarritoPersonaEntity> findByPersona(PersonaEntity persona);
}
