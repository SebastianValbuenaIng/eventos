package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.EstadoCompraEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EstadoCompraRepository extends CrudRepository<EstadoCompraEntity, Integer> {
    Optional<EstadoCompraEntity> findByDescripcion(String descripcion);
}
