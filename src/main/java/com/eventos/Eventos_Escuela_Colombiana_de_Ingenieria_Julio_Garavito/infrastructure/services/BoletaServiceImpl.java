package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.services;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.request.BoletaPersonaRequest;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.BoletaCarritoResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.BoletaPagadaResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.BoletaRolResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.*;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.*;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.BoletaService;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.IdNotFoundException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.MessageBadRequestException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.ServerErrorException;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
@Transactional
public class BoletaServiceImpl implements BoletaService {
    private final BoletaRepository boletaRepository;
    private final RolRepository rolRepository;
    private final EventoRepository eventoRepository;
    private final CarritoPersonaRepository carritoPersonaRepository;
    private final PersonaRepository personaRepository;
    private final CompraRepository compraRepository;
    private final BoletaCompraRepository boletaCompraRepository;
    private final EstadoCompraRepository estadoCompraRepository;

    @Override
    public BoletaRolResponse getBoletasByRol(String rol) {
        Map<String, Integer> boletas = new HashMap<>();

        // By Rol
        RolEntity rolFound = rolRepository
                .findByDescripcion(rol.equalsIgnoreCase("estudiantes") ? "estudiante" : rol.toLowerCase())
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

        LocalDate fechaFin = boletaFound.getEvento().getFecha_final_regular();
        LocalDate fechaActual = LocalDate.now();

//        if (fechaActual.isEqual(boletaFound.getEvento().getFecha_inicio_preventa()) ||
//            fechaActual.isEqual(boletaFound.getEvento().getFecha_final_preventa()) ||
//            (
//                    fechaActual.isAfter(boletaFound.getEvento().getFecha_inicio_preventa()) &&
//                    fechaActual.isBefore(boletaFound.getEvento().getFecha_final_preventa())
//            ) ||
//            fechaActual.isBefore(boletaFound.getEvento().getFecha_inicio_preventa())
//        ) {
//            fechaFin = boletaFound.getEvento().getFecha_final_preventa();
//        } else if (
//                fechaActual.isEqual(boletaFound.getEvento().getFecha_inicio_regular()) ||
//                fechaActual.isEqual(boletaFound.getEvento().getFecha_final_regular()) ||
//                (
//                        fechaActual.isAfter(boletaFound.getEvento().getFecha_inicio_regular()) &&
//                        fechaActual.isBefore(boletaFound.getEvento().getFecha_final_regular())
//                )
//        ) {
//            fechaFin = boletaFound.getEvento().getFecha_final_regular();
//        } else {
//            fechaFin = LocalDate.now();
//        }

        return BoletaRolResponse
                .builder()
                .precios_boletas(boletas)
                .id_boleta_principal(boletaFound.getId())
                .id_boleta_invitado(boletaInvitadoFound.getId())
                .fecha_venta_fin((!fechaActual.equals(fechaFin) && !fechaActual.isAfter(fechaFin)))
                .build();
    }

    @Override
    public Map<String, String> agregarAlCarrito(BoletaPersonaRequest boletaPersonaRequest) {
        validarRequestAgregar(boletaPersonaRequest);

        BoletaEntity foundBoleta = boletaRepository.findById(boletaPersonaRequest.getId_boleta())
                .orElseThrow(() -> new IdNotFoundException("boleta"));

        if (!foundBoleta.getRol().getDescripcion().equals("Invitado") && boletaPersonaRequest.getCantidad_boletas() > 1)
            throw new MessageBadRequestException("No puedes agregar más de una boleta a ese rol");

        PersonaEntity persona;

        Optional<PersonaEntity> foundPersona = personaRepository.findByDocumento(boletaPersonaRequest.getDocumento());

        if (foundPersona.isPresent() && (foundPersona.get().getCorreo() == null || foundPersona.get().getCorreo().isEmpty())) {
            foundPersona.get().setCorreo(boletaPersonaRequest.getCorreo());
            personaRepository.save(foundPersona.get());
        }

        if (foundPersona.isPresent() && (foundPersona.get().getTelefono() == null || foundPersona.get().getTelefono().isEmpty())) {
            foundPersona.get().setTelefono(boletaPersonaRequest.getTelefono());
            personaRepository.save(foundPersona.get());
        }

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
                    .documento(boletaPersonaRequest.getDocumento())
                    .rol(foundRol)
                    .build();

            persona = personaRepository.save(newPersona);
        }

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

        Map<String, String> message = getCompraPersona(persona);
        if (message != null) return message;

        return Map.of("message", "proceso completado correctamente");
    }

    private Map<String, String> getCompraPersona(PersonaEntity persona) {
        EstadoCompraEntity estadoCompra = estadoCompraRepository
                .findByDescripcion("En espera")
                .orElseThrow(() -> new IdNotFoundException("estado_compra"));

        Optional<CompraEntity> compraFound = compraRepository
                .findByEstadoCompraAndPersona(estadoCompra, persona);

        int valorTotal = getValorTotal(persona);

        if (compraFound.isPresent()) {
            compraFound.get().setValor(valorTotal);
            compraRepository.save(compraFound.get());

            return Map.of("message", "proceso completado correctamente");
        }

        int numeroDocumentoPersonaSinLetras = removeLetters(persona.getDocumento());

        CompraEntity compra = CompraEntity
                .builder()
                .fecha_creacion(LocalDateTime.now())
                .fecha_pago(null)
                .valor(valorTotal)
                .persona(persona)
                .estadoCompra(estadoCompra)
                .numeroReferencia((long) numeroDocumentoPersonaSinLetras)
                .build();

        compraRepository.save(compra);
        return null;
    }

    private int getValorTotal(PersonaEntity personaFound) {
        List<CarritoPersonaEntity> carritoPersona = carritoPersonaRepository.findByPersona(personaFound);
        return getValorTotal(carritoPersona);
    }

    private static int getValorTotal(List<CarritoPersonaEntity> carritoPersona) {
        List<Integer> valoresFiltrados = carritoPersona.stream()
                .filter(carritoPersonaEntity -> !carritoPersonaEntity.getBoleta().getRol().getDescripcion().equals("Estudiante"))
                .map(carritoPersonaEntity -> carritoPersonaEntity.getBoleta().getValor())
                .toList();

        int valorConDescuentoParesInvitado = getValorConDescuento(valoresFiltrados);

        int valorBoleta = carritoPersona.stream()
                .filter(carritoPersonaEntity -> carritoPersonaEntity.getBoleta().getRol().getDescripcion().equals("Estudiante"))
                .map(carritoPersonaEntity -> carritoPersonaEntity.getBoleta().getValor())
                .findFirst()
                .orElse(0);

        return valorConDescuentoParesInvitado + valorBoleta;
    }

    private static int getValorConDescuento(List<Integer> valoresFiltrados) {
        int valorConDescuento = 0;

        for (int i = 0; i < valoresFiltrados.size(); i++) {
            int valorBoleta = valoresFiltrados.get(i);

            if (i % 2 == 0 && i + 1 < valoresFiltrados.size()) {
                // Si es parte de un par, restar 10,000 a cada boleta del par
                valorConDescuento += valorBoleta - 10000;
            } else if (i % 2 == 1) {
                // Resta 10,000 a la segunda boleta del par
                valorConDescuento += valorBoleta - 10000;
            } else {
                // Si es la última boleta en un caso impar, mantener su valor original
                valorConDescuento += valorBoleta;
            }
        }
        return valorConDescuento;
    }

    private void validarRequestAgregar(BoletaPersonaRequest boletaPersonaRequest) {
        validNull(boletaPersonaRequest.getCorreo(), "correo");
        validNull(boletaPersonaRequest.getCorreo(), "telefono");
        validNull(boletaPersonaRequest.getCorreo(), "nombre");
        validNull(boletaPersonaRequest.getCorreo(), "documento");
        validNull(boletaPersonaRequest.getCorreo(), "rol");
        validNull(boletaPersonaRequest.getId_boleta(), "id_boleta");

        validEmpty(boletaPersonaRequest.getCorreo(), "correo");
        validEmpty(boletaPersonaRequest.getCorreo(), "telefono");
        validEmpty(boletaPersonaRequest.getCorreo(), "nombre");
        validEmpty(boletaPersonaRequest.getCorreo(), "documento");
        validEmpty(boletaPersonaRequest.getCorreo(), "rol");
    }

    private static int removeLetters(String input) {
        // Usamos una expresión regular para reemplazar todas las letras con una cadena vacía
        String numbersOnly = input.replaceAll("[a-zA-Z]", "");

        // Convertimos la cadena resultants en un entero
        // Agregamos un control para evitar excepciones si la cadena está vacía
        if (!numbersOnly.isEmpty()) {
            return Integer.parseInt(numbersOnly);
        } else {
            // Devolvemos 0 si no hay números en la cadena original
            throw new ServerErrorException();
        }
    }

    @Override
    public Map<String, String> eliminarDelCarrito(Integer id_carrito_persona, String documento) {
        PersonaEntity personaFound = personaRepository.findByDocumento(documento).orElseThrow(() -> new IdNotFoundException("documento"));

        CarritoPersonaEntity foundCarritoPersonaById = carritoPersonaRepository
                .findById(id_carrito_persona)
                .orElseThrow(() -> new IdNotFoundException("carrito_persona"));

// Eliminar la boleta seleccionada del carrito
        carritoPersonaRepository.delete(foundCarritoPersonaById);

// Recuperar las boletas restantes en el carrito
        List<CarritoPersonaEntity> carritoPersona = carritoPersonaRepository.findByPersona(personaFound);

// Buscar el estado de la compra "En espera"
        EstadoCompraEntity estadoCompraEnEspera = estadoCompraRepository.findByDescripcion("En espera")
                .orElseThrow(() -> new IdNotFoundException("estado_compra"));

        // Verificar si la compra existe y está en estado "En espera"
        Optional<CompraEntity> compra = compraRepository
                .findByNumeroReferenciaAndEstadoCompra(Long.valueOf(personaFound.getDocumento()), estadoCompraEnEspera);

        if (!carritoPersona.isEmpty() && compra.isPresent()) {
            AtomicInteger valorTotal = new AtomicInteger();
            List<CarritoPersonaEntity> boletasConDescuento = new ArrayList<>();

            // Recalcular el valor total y seleccionar solo las boletas con rol diferente de "estudiante"
            carritoPersona.forEach(carritoPersonaEntity -> {
                int valorBoleta = carritoPersonaEntity.getBoleta().getValor();
                valorTotal.addAndGet(valorBoleta);

                // Solo consideramos las boletas que no son de estudiantes
                if (!"Estudiante".equals(carritoPersonaEntity.getBoleta().getRol().getDescripcion())) {
                    boletasConDescuento.add(carritoPersonaEntity);
                }
            });

            // Aplicar descuento solo si hay pares de boletas sin rol de "estudiante"
            int cantidadBoletasConDescuento = boletasConDescuento.size();
            int descuento = 0;

            if (cantidadBoletasConDescuento % 2 == 0) {
                // Si hay un número par de boletas que no son de "estudiante", aplicamos el descuento
                descuento = (cantidadBoletasConDescuento / 2) * 20000;  // 20,000 por cada par
            } else if (cantidadBoletasConDescuento > 1) {
                // Si hay un número impar de boletas no "estudiante", aplicamos el descuento a los pares
                descuento = ((cantidadBoletasConDescuento - 1) / 2) * 20000;  // Aplicar el descuento solo para los pares
            }

            // Recalcular el valor total después del descuento
            int valorFinal = valorTotal.get() - descuento;

            // Actualizar el valor de la compra
            compra.get().setValor(valorFinal);
            compraRepository.save(compra.get());

            System.out.println("Valor final de la compra: " + valorFinal);
        }


        if (carritoPersona.isEmpty()) {
            if (compra.isPresent()) {
                EstadoCompraEntity estadoCompraCancelado = estadoCompraRepository.findByDescripcion("Eliminado")
                        .orElseThrow(() -> new IdNotFoundException("estado_compra"));

                compra.get().setEstadoCompra(estadoCompraCancelado);
                compraRepository.save(compra.get());
            }
        }

        return Map.of("message", "proceso completado correctamente");
    }

    @Override
    public Map<String, Object> getBoletasCarritoPersona(String nro_documento) {
        PersonaEntity personaFound = personaRepository
                .findByDocumento(nro_documento)
                .orElseThrow(() -> new IdNotFoundException("persona"));

        List<CarritoPersonaEntity> carritoPersona = carritoPersonaRepository.findByPersona(personaFound);

        Map<String, Object> returnBoletasCarritoPersona = new HashMap<>();

        List<BoletaCarritoResponse> boletaCarrito = carritoPersona.stream()
                .map(carritoPersonaEntity -> BoletaCarritoResponse
                        .builder()
                        .boleta_principal(!Objects.equals(carritoPersonaEntity.getBoleta().getRol().getDescripcion(), "Invitado"))
                        .valor(carritoPersonaEntity.getBoleta().getValor())
                        .id_boleta_carrito(carritoPersonaEntity.getId())
                        .build())
                .toList();

        EstadoCompraEntity estadoCompra = estadoCompraRepository
                .findByDescripcion("En espera")
                .orElseThrow(() -> new IdNotFoundException("estado_compra"));

        Optional<CompraEntity> compraFound = compraRepository
                .findByEstadoCompraAndPersona(estadoCompra, personaFound);

        Long numero_referencia = null;

        if (compraFound.isPresent()) numero_referencia = compraFound.get().getNumeroReferencia();

        returnBoletasCarritoPersona.put("boletas", boletaCarrito);
        returnBoletasCarritoPersona.put("numero_referencia", numero_referencia);

        return returnBoletasCarritoPersona;
    }

    @Override
    public List<BoletaPagadaResponse> getBoletasPagadaPersona(String nro_documento) {
        PersonaEntity persona = personaRepository
                .findByDocumento(nro_documento)
                .orElseThrow(() -> new IdNotFoundException("persona"));

        List<CompraEntity> comprasPersona = compraRepository.findAllByPersona(persona);

        return comprasPersona
                .stream()
                .filter(compraEntity -> compraEntity.getEstadoCompra().getDescripcion().equals("Completado"))
                .map(compraEntity -> {
                    List<BoletaCompraEntity> boletaCompra = boletaCompraRepository.findAllByCompra(compraEntity);

                    long boletasInvitados = boletaCompra
                            .stream()
                            .filter(boletaCompraEntity -> boletaCompraEntity.getBoleta().getRol().getDescripcion().equals("Invitado"))
                            .count();

                    Optional<BoletaCompraEntity> boletaPrincipal = boletaCompra
                            .stream()
                            .filter(boletaCompraEntity -> !boletaCompraEntity.getBoleta().getRol().getDescripcion().equals("Invitado"))
                            .findFirst();

                    return BoletaPagadaResponse
                            .builder()
                            .nombre(persona.getNombre())
                            .fecha_pago(compraEntity.getFecha_pago())
                            .boleta_principal(boletaPrincipal.isPresent())
                            .cantidad_boletas_invitados((int) boletasInvitados)
                            .valor(compraEntity.getValor())
                            .build();
                })
                .toList();
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
