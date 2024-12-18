package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.AdministradorEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.EventoEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AdministradorRepository extends CrudRepository<AdministradorEntity, Integer> {
    Optional<AdministradorEntity> findByCorreo(String correo);
    Optional<AdministradorEntity> findByCorreoAndEvento(String correo, EventoEntity evento);
}
