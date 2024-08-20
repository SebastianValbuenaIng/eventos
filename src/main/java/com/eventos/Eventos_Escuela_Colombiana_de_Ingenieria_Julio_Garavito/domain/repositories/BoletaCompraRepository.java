package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.BoletaCompraEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.CompraEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BoletaCompraRepository extends CrudRepository<BoletaCompraEntity, Integer> {
    List<BoletaCompraEntity> findAllByCompra(CompraEntity compra);
}
