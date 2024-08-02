package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.services;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.request.BoletaPersonaRequest;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.BoletaRolResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.*;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.*;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.BoletaService;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.IdNotFoundException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.MessageBadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class BoletaServiceImpl implements BoletaService {
    private final BoletaRepository boletaRepository;
    private final RolRepository rolRepository;
    private final EventoRepository eventoRepository;
    private final CarritoPersonaRepository carritoPersonaRepository;
    private final PersonaRepository personaRepository;

    @Override
    public BoletaRolResponse getBoletasByRol(String rol) {
        Map<String, Integer> boletas = new HashMap<>();

        // By Rol
        RolEntity rolFound = rolRepository
                .findByDescripcion(rol)
                .orElseThrow(() -> new IdNotFoundException("rol"));

        BoletaEntity boletaFound = boletaRepository
                .findByRol(rolFound)
                .orElseThrow(() -> new IdNotFoundException("boleta"));

        // By Invitado
        RolEntity rolInvitadoFound = rolRepository
                .findByDescripcion("Invitado")
                .orElseThrow(() -> new IdNotFoundException("rol"));

        BoletaEntity boletaInvitadoFound = boletaRepository
                .findByRol(rolInvitadoFound)
                .orElseThrow(() -> new IdNotFoundException("boleta"));

        boletas.put("boleta_principal", boletaFound.getValor());
        boletas.put("boleta_invitado", boletaInvitadoFound.getValor());

        // * Precio total restando 10.000 COP por boleta
        boletas.put("boleta_doble", (boletaInvitadoFound.getValor() - 10000) * 2);

        LocalDate fechaFin;
        LocalDate fechaActual = LocalDate.now().plusMonths(2).plusDays(2);

        if (fechaActual.isEqual(boletaFound.getEvento().getFecha_inicio_preventa()) ||
            fechaActual.isEqual(boletaFound.getEvento().getFecha_final_preventa()) ||
            (
                    fechaActual.isAfter(boletaFound.getEvento().getFecha_inicio_preventa()) &&
                    fechaActual.isBefore(boletaFound.getEvento().getFecha_final_preventa())
            ) ||
            fechaActual.isBefore(boletaFound.getEvento().getFecha_inicio_preventa())
        ) {
            fechaFin = boletaFound.getEvento().getFecha_final_preventa();
        } else if (
                fechaActual.isEqual(boletaFound.getEvento().getFecha_inicio_regular()) ||
                fechaActual.isEqual(boletaFound.getEvento().getFecha_final_regular()) ||
                (
                        fechaActual.isAfter(boletaFound.getEvento().getFecha_inicio_regular()) &&
                        fechaActual.isBefore(boletaFound.getEvento().getFecha_final_regular())
                )
        ) {
            fechaFin = boletaFound.getEvento().getFecha_final_regular();
        } else {
            fechaFin = LocalDate.now();
        }

        return BoletaRolResponse
                .builder()
                .precios_boletas(boletas)
                .fecha_venta_fin(fechaFin)
                .build();
    }

    @Override
    public Map<String, String> agregarAlCarrito(BoletaPersonaRequest boletaPersonaRequest) {
        validarRequestAgregar(boletaPersonaRequest);

        // TODO: Estar pendiente del id rol para la boleta
        if (boletaPersonaRequest.getId_boleta() != 5 && boletaPersonaRequest.getCantidad_boletas() > 1)
            throw new MessageBadRequestException("No puedes agregar más de una boleta a ese rol");

        PersonaEntity persona;

        Optional<PersonaEntity> foundPersona = personaRepository.findByDocumento(boletaPersonaRequest.getDocumento());

        if (foundPersona.isPresent()) persona = foundPersona.get();
        else {
            RolEntity foundRol = rolRepository
                    .findByDescripcion(boletaPersonaRequest.getRol().toLowerCase())
                    .orElseThrow(() -> new IdNotFoundException("rol"));

            PersonaEntity newPersona = PersonaEntity
                    .builder()
                    .correo(boletaPersonaRequest.getCorreo())
                    .nombre(boletaPersonaRequest.getNombre())
                    .telefono(boletaPersonaRequest.getTelefono())
                    .correo_fact_e(boletaPersonaRequest.getCorreo_facturacion())
                    .documento(boletaPersonaRequest.getDocumento())
                    .rol(foundRol)
                    .build();

            persona = personaRepository.save(newPersona);
        }

        BoletaEntity foundBoleta = boletaRepository.findById(boletaPersonaRequest.getId_boleta())
                .orElseThrow(() -> new IdNotFoundException("boleta"));

        List<CarritoPersonaEntity> carritoPersona = carritoPersonaRepository.findByPersona(persona);

        Optional<CarritoPersonaEntity> carritoPersonaByRol = carritoPersona
                .stream()
                .filter(carritoPersonaEntity -> carritoPersonaEntity.getBoleta().getRol().getDescripcion()
                        .equals(foundBoleta.getRol().getDescripcion())
                )
                .filter(carritoPersonaEntity -> !carritoPersonaEntity.getBoleta().getRol().getDescripcion().equals("Invitado"))
                .findFirst();

        if (carritoPersonaByRol.isPresent())
            throw new MessageBadRequestException("Ya existe una boleta para ese rol, solo puede agregar para invitados");

        for (int i = 0; i < boletaPersonaRequest.getCantidad_boletas(); i++) {
            CarritoPersonaEntity newCarritoPersona = CarritoPersonaEntity
                    .builder()
                    .persona(persona)
                    .fecha_reserva(LocalDateTime.now())
                    .boleta(foundBoleta)
                    .build();

            carritoPersonaRepository.save(newCarritoPersona);
        }

        return Map.of("message", "proceso completado correctamente");
    }

    private void validarRequestAgregar(BoletaPersonaRequest boletaPersonaRequest) {
        validNull(boletaPersonaRequest.getCorreo(), "correo");
        validNull(boletaPersonaRequest.getCorreo(), "telefono");
        validNull(boletaPersonaRequest.getCorreo(), "nombre");
        validNull(boletaPersonaRequest.getCorreo(), "documento");
        validNull(boletaPersonaRequest.getCorreo(), "rol");
        validNull(boletaPersonaRequest.getCorreo(), "correo_facturacion");
        validNull(boletaPersonaRequest.getId_boleta(), "id_boleta");

        validEmpty(boletaPersonaRequest.getCorreo(), "correo");
        validEmpty(boletaPersonaRequest.getCorreo(), "telefono");
        validEmpty(boletaPersonaRequest.getCorreo(), "nombre");
        validEmpty(boletaPersonaRequest.getCorreo(), "documento");
        validEmpty(boletaPersonaRequest.getCorreo(), "rol");
        validEmpty(boletaPersonaRequest.getCorreo(), "correo_facturacion");
    }

    @Override
    public Map<String, String> eliminarDelCarrito(Integer id_carrito_persona) {
        CarritoPersonaEntity foundCarritoPersonaById = carritoPersonaRepository
                .findById(id_carrito_persona)
                .orElseThrow(() -> new IdNotFoundException("carrito_persona"));

        carritoPersonaRepository.delete(foundCarritoPersonaById);

        return Map.of("message", "proceso completado correctamente");
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *", zone = "America/Bogota")
    public void changeValuesBoletaByDayFechaInicioRegular() {
        EventoEntity evento = eventoRepository
                .findById(1)
                .orElseThrow(() -> new IdNotFoundException("evento"));

        LocalDate fechaActual = LocalDate.now();

        if (fechaActual.isEqual(evento.getFecha_inicio_regular())) {
            // Graduado
            RolEntity rolGraduadoFound = rolRepository
                    .findByDescripcion("Graduado")
                    .orElseThrow(() -> new IdNotFoundException("rol"));

            BoletaEntity boletaGraduadoFound = boletaRepository
                    .findByRol(rolGraduadoFound)
                    .orElseThrow(() -> new IdNotFoundException("boleta"));

            boletaGraduadoFound.setValor(90000);
            boletaRepository.save(boletaGraduadoFound);

            // Estudiante
            RolEntity rolEstudianteFound = rolRepository
                    .findByDescripcion("Estudiante")
                    .orElseThrow(() -> new IdNotFoundException("rol"));

            BoletaEntity boletaEstudianteFound = boletaRepository
                    .findByRol(rolEstudianteFound)
                    .orElseThrow(() -> new IdNotFoundException("boleta"));

            boletaEstudianteFound.setValor(65000);
            boletaRepository.save(boletaEstudianteFound);

            // Profesor
            RolEntity rolProfesorFound = rolRepository
                    .findByDescripcion("Profesor")
                    .orElseThrow(() -> new IdNotFoundException("rol"));

            BoletaEntity boletaProfesorFound = boletaRepository
                    .findByRol(rolProfesorFound)
                    .orElseThrow(() -> new IdNotFoundException("boleta"));

            boletaProfesorFound.setValor(90000);
            boletaRepository.save(boletaProfesorFound);

            // Administrativo
            RolEntity rolAdministrativoFound = rolRepository
                    .findByDescripcion("Administrativo")
                    .orElseThrow(() -> new IdNotFoundException("rol"));

            BoletaEntity boletaAdministrativoFound = boletaRepository
                    .findByRol(rolAdministrativoFound)
                    .orElseThrow(() -> new IdNotFoundException("boleta"));

            boletaAdministrativoFound.setValor(90000);
            boletaRepository.save(boletaAdministrativoFound);

            // Invitado
            RolEntity rolInvitadoFound = rolRepository
                    .findByDescripcion("Invitado")
                    .orElseThrow(() -> new IdNotFoundException("rol"));

            BoletaEntity boletaInvitadoFound = boletaRepository
                    .findByRol(rolInvitadoFound)
                    .orElseThrow(() -> new IdNotFoundException("boleta"));

            boletaInvitadoFound.setValor(90000);
            boletaRepository.save(boletaInvitadoFound);
        }
    }

    private void validNull(Object requestObject, String campo) {
        if (requestObject == null)
            throw new MessageBadRequestException("El campo " + campo + " no puede ser nulo");
    }

    private void validEmpty(String requestString, String campo) {
        if (requestString.isEmpty())
            throw new MessageBadRequestException("El campo " + campo + " no puede estar vacío");
    }
}
