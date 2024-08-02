package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.BoletaEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.RolEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BoletaRepository extends CrudRepository<BoletaEntity, Integer> {
    Optional<BoletaEntity> findByRol(RolEntity rol);
}
