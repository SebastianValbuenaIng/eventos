package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.services;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.AdminBoletaPagadaResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.AdminCompraResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.CompraAdminResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.BoletaCompraEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.CarritoPersonaEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.CompraEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.AdministradorRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.BoletaCompraRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.CarritoPersonaRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.CompraRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.AdministradorService;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.IdNotFoundException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.NoPermissionsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AdministradorServiceImpl implements AdministradorService {
    private final AdministradorRepository administradorRepository;
    private final CompraRepository compraRepository;
    private final BoletaCompraRepository boletaCompraRepository;
    private final CarritoPersonaRepository carritoPersonaRepository;

    @Override
    public List<AdminBoletaPagadaResponse> getBoletasAdmin(String correo) {
        administradorRepository.findByCorreo(correo).orElseThrow(NoPermissionsException::new);

        List<CompraEntity> findAllCompras = (List<CompraEntity>) compraRepository.findAll();

        List<CarritoPersonaEntity> findAllCarritoPersona = (List<CarritoPersonaEntity>) carritoPersonaRepository.findAll();

        List<AdminBoletaPagadaResponse> boletasAdmin = new ArrayList<>();

        // Boletas en el carrito
        findAllCarritoPersona.forEach(carritoPersonaEntity -> {
            AdminBoletaPagadaResponse boletaPagadaResponse = AdminBoletaPagadaResponse
                    .builder()
                    .nombre(carritoPersonaEntity.getPersona().getNombre())
                    .rol(carritoPersonaEntity.getBoleta().getRol().getDescripcion())
                    .telefono(carritoPersonaEntity.getPersona().getTelefono())
                    .correo(carritoPersonaEntity.getPersona().getCorreo())
                    .valor(carritoPersonaEntity.getBoleta().getValor())
                    .build();

            boletasAdmin.add(boletaPagadaResponse);
        });

        // Boletas pagadas
        findAllCompras.forEach(compraEntity -> {
            List<BoletaCompraEntity> boletasCompra = boletaCompraRepository.findAllByCompra(compraEntity);

            boletasCompra.forEach(boletaCompraEntity -> {
                AdminBoletaPagadaResponse boletaPagadaResponse = AdminBoletaPagadaResponse
                        .builder()
                        .fecha_pago(compraEntity.getFecha_pago())
                        .nombre(compraEntity.getPersona().getNombre())
                        .rol(boletaCompraEntity.getBoleta().getRol().getDescripcion())
                        .consecutivo_boleta(boletaCompraEntity.getConsecutivoBoleta())
                        .telefono(compraEntity.getPersona().getTelefono())
                        .correo(compraEntity.getPersona().getCorreo())
                        .valor(boletaCompraEntity.getValor())
                        .build();

                boletasAdmin.add(boletaPagadaResponse);
            });
        });

        return boletasAdmin;
    }

    @Override
    public List<AdminCompraResponse> getCompras(String correo) {
        administradorRepository.findByCorreo(correo).orElseThrow(NoPermissionsException::new);

        List<CompraEntity> findAllCompras = (List<CompraEntity>) compraRepository.findAll();

        List<AdminCompraResponse> boletasAdmin = new ArrayList<>();

        findAllCompras.forEach(compraEntity -> {
          AdminCompraResponse adminCompraResponse = AdminCompraResponse
                  .builder()
                  .id(compraEntity.getId())
                  .nombre(compraEntity.getPersona().getNombre())
                  .fecha_pago(compraEntity.getFecha_pago() == null ? null : compraEntity.getFecha_pago())
                  .valor(compraEntity.getValor())
                  .estado_pago(compraEntity.getEstadoCompra().getDescripcion())
                  .rol(compraEntity.getPersona().getRol().getDescripcion())
                  .build();

          boletasAdmin.add(adminCompraResponse);
        });

        return boletasAdmin;
    }

    @Override
    public CompraAdminResponse getCompraPersona(String correo, Integer id) {
        administradorRepository.findByCorreo(correo).orElseThrow(NoPermissionsException::new);

        CompraEntity compraFound = compraRepository
                .findById(id)
                .orElseThrow(() -> new IdNotFoundException("compra"));

        List<BoletaCompraEntity> boletaCompra = boletaCompraRepository.findAllByCompra(compraFound);

        List<Map<String, Object>> boletas = new ArrayList<>();

        boletaCompra.forEach(boletaCompraEntity -> {
           Map<String, Object> boleta = new HashMap<>();

           boleta.put("consecutivo_boleta", boletaCompraEntity.getConsecutivoBoleta());
           boleta.put("valor_boleta", boletaCompraEntity.getValor());
           boleta.put("rol_boleta", boletaCompraEntity.getBoleta().getRol().getDescripcion());

           boletas.add(boleta);
        });

        return CompraAdminResponse
                .builder()
                .persona(compraFound.getPersona())
                .fecha_pago(compraFound.getFecha_pago())
                .valor_total(compraFound.getValor())
                .boletas(boletas)
                .build();
    }
}
