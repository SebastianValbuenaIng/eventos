package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.CompraEntity;
import org.springframework.data.repository.CrudRepository;

public interface CompraRepository extends CrudRepository<CompraEntity, Integer> {
}
