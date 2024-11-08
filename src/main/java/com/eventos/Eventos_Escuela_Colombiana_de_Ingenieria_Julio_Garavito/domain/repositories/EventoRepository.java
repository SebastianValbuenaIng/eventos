package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.EventoEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EventoRepository extends CrudRepository<EventoEntity, Integer> {
    Optional<EventoEntity> findByNombre(String nombre);
}
