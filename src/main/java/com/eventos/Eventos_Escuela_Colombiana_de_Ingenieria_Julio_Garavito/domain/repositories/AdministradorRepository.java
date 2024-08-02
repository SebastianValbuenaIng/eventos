package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.AdministradorEntity;
import org.springframework.data.repository.CrudRepository;

public interface AdministradorRepository extends CrudRepository<AdministradorEntity, Integer> {
}
