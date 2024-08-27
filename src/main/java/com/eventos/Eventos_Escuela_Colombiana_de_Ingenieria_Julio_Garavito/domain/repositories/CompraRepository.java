package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.CompraEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.EstadoCompraEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.PersonaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CompraRepository extends CrudRepository<CompraEntity, Integer> {
    List<CompraEntity> findAllByPersona(PersonaEntity persona);
    Optional<CompraEntity> findByEstadoCompraAndAndPersona(EstadoCompraEntity estadoCompra, PersonaEntity persona);
    Optional<CompraEntity> findByNumeroReferenciaAndEstadoCompra(Long numeroReferencia, EstadoCompraEntity estadoCompra);
}
