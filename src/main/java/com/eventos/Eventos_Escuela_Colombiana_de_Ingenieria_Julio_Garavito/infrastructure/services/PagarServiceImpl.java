package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.services;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.*;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.*;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.PagarService;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.IdNotFoundException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.MessageBadRequestException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.ServerErrorException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.helpers.ReadHTMLTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PagarServiceImpl implements PagarService {
    private final PersonaRepository personaRepository;
    private final CarritoPersonaRepository carritoPersonaRepository;
    private final CompraRepository compraRepository;
    private final EstadoCompraRepository estadoCompraRepository;
    private final BoletaCompraRepository boletaCompraRepository;
    private final JavaMailSender javaMailSender;
    private final Environment environment;
    private final ReadHTMLTemplate readHtmlTemplate;

    @Override
    public Map<String, Long> pagar(String documento, Integer valor) {
        PersonaEntity personaFound = personaRepository
                .findByDocumento(documento)
                .orElseThrow(() -> new IdNotFoundException("persona"));

        EstadoCompraEntity estadoCompra = estadoCompraRepository
                .findByDescripcion("En espera")
                .orElseThrow(() -> new IdNotFoundException("estado_compra"));

        Optional<CompraEntity> compraFound = compraRepository
                .findByEstadoCompraAndAndPersona(estadoCompra, personaFound);

        int valorTotal = getValorTotal(valor, personaFound);

        if (compraFound.isPresent()) {
            compraFound.get().setValor(valorTotal);
            compraRepository.save(compraFound.get());

            return Map.of("id_referencia", compraFound.get().getNumeroReferencia());
        }

        int numeroDocumentoPersonaSinLetras = removeLetters(personaFound.getDocumento());

        CompraEntity compra = CompraEntity
                .builder()
                .fecha_creacion(LocalDateTime.now())
                .fecha_pago(null)
                .valor(valorTotal)
                .persona(personaFound)
                .estadoCompra(estadoCompra)
                .numeroReferencia((long) numeroDocumentoPersonaSinLetras)
                .build();

        CompraEntity compraSaved = compraRepository.save(compra);

        return Map.of("id_referencia", compraSaved.getNumeroReferencia());
    }

    private int getValorTotal(Integer valor, PersonaEntity personaFound) {
        List<CarritoPersonaEntity> carritoPersona = carritoPersonaRepository.findByPersona(personaFound);
        int valorTotal = getValorTotal(carritoPersona);

        if (valor != valorTotal) throw new MessageBadRequestException("El valor no es el adecuado");
        // TODO: Descomentar
        return valorTotal;
//        return 1000;
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

    @Override
    public void generatePay(String descripcion, String value, String estado_pol) {
        if (!Objects.equals(estado_pol, "4")) return;

        EstadoCompraEntity estadoCompraEnEspera = estadoCompraRepository
                .findByDescripcion("En espera")
                .orElseThrow(() -> new IdNotFoundException("estado_compra"));

        CompraEntity compraFound = compraRepository
                .findByNumeroReferenciaAndEstadoCompra(Long.valueOf(descripcion.split("#")[1]), estadoCompraEnEspera)
                .orElseThrow(() -> new IdNotFoundException("compra"));

//        if (compraFound.getEstadoCompra().getDescripcion().equals("Completado")) return;

        PersonaEntity personaFound = personaRepository
                .findByDocumento(compraFound.getPersona().getDocumento())
                .orElseThrow(() -> new IdNotFoundException("persona"));

        int valorRecibido = Integer.parseInt(value.split("\\.")[0]);

        boolean validPay = (compraFound.getValor() == valorRecibido);
        if (!validPay) return;

        // * Limpiar del carrito de la persona e insertar en boleta_compra
        List<BoletaCompraEntity> boletasCompras = insertsBoletaCompraAndDeleteCarritoPersona(personaFound, compraFound);

        boletasCompras.forEach(boletaCompraEntity -> System.out.println(boletaCompraEntity.getConsecutivoBoleta()));

        EstadoCompraEntity estadoCompraCompletado = estadoCompraRepository
                .findByDescripcion("Completado")
                .orElseThrow(() -> new IdNotFoundException("estado_compra"));

        compraFound.setEstadoCompra(estadoCompraCompletado);
        compraFound.setFecha_pago(LocalDateTime.now());
        compraRepository.save(compraFound);

        String htmlBody = getEmail(boletasCompras, personaFound, compraFound);

        // * Enviar correo
        enviarCorreo(htmlBody, personaFound.getCorreo());
    }

    @Override
    public void enviarCorreoPrueba() {
        EstadoCompraEntity estadoCompraEnEspera = estadoCompraRepository
                .findByDescripcion("En espera")
                .orElseThrow(() -> new IdNotFoundException("estado_compra"));

        CompraEntity compraFound = compraRepository
                .findByNumeroReferenciaAndEstadoCompra(Long.valueOf("90991193044098"), estadoCompraEnEspera)
                .orElseThrow(() -> new IdNotFoundException("compra"));

        PersonaEntity personaFound = personaRepository
                .findByDocumento("1193044098")
                .orElseThrow(() -> new IdNotFoundException("persona"));

        List<BoletaCompraEntity> boletasCompras = boletaCompraRepository.findAll();

        String htmlBody = getEmail(boletasCompras, personaFound, compraFound);

        // * Enviar correo
        enviarCorreo(htmlBody, personaFound.getCorreo());
    }

    private static String getEmail(List<BoletaCompraEntity> boletasCompras, PersonaEntity personaFound, CompraEntity compraFound) {
        StringBuilder boletasPersonaEmail = new StringBuilder();

        for (BoletaCompraEntity boletasCompra : boletasCompras) {
            boletasPersonaEmail.append(String.format("""
                    <div class="container-boletas">
                        <img src="https://res.cloudinary.com/dskibbwgt/image/upload/c_scale,h_85,w_265/eg4xtvp1iibz9c38nb9g.jpg" alt="imagen boleta" className='image-boleta'/>
                        <p class="texto-consecutivo-boleta"><b> Boleta Nº: %s </b></p>
                    </div>
                    <br />
                    <hr>
                    <br />
                    """, boletasCompra.getConsecutivoBoleta()));
        }

        NumberFormat formatoCOP = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        return String.format("""
                <p class="texto">Hola, %s: </p>
                <p class="texto">Se le informa que se ha recibido su pago correctamente por un valor de: <b>%s COP</b>.</p>
                <p>El número de sus boletas son: </p>
                <br />
                %s
                """, personaFound.getNombre(), formatoCOP.format(compraFound.getValor()), boletasPersonaEmail);
    }

    private List<BoletaCompraEntity> insertsBoletaCompraAndDeleteCarritoPersona(PersonaEntity personaFound, CompraEntity compraFound) {
        List<CarritoPersonaEntity> carritoPersona = carritoPersonaRepository.findByPersona(personaFound);

        List<BoletaCompraEntity> boletaCompraDb = new ArrayList<>();
        AtomicInteger contador = new AtomicInteger(0);

        for (CarritoPersonaEntity carritoPersonaEntity : carritoPersona) {
            int valorOriginal = carritoPersonaEntity.getBoleta().getValor();
            int valorConDescuento = valorOriginal;

            boolean esEstudiante = carritoPersonaEntity.getBoleta().getRol().getDescripcion().equals("Estudiante");

            if (!esEstudiante) {
                // Aplica descuento si no es estudiante y es parte de un par
                if (contador.incrementAndGet() % 2 == 0) {
                    valorConDescuento -= 10000;
                    boletaCompraDb.get(boletaCompraDb.size() - 1).setValor(valorOriginal - 10000);  // Actualizar la boleta anterior también
                }
            }

            // Crear la entidad BoletaCompraEntity para todas las boletas
            BoletaCompraEntity boletaCompra = BoletaCompraEntity
                    .builder()
                    .valor(valorConDescuento)
                    .boleta(carritoPersonaEntity.getBoleta())
                    .id(carritoPersonaEntity.getId())
                    .compra(compraFound)
                    .build();

            boletaCompraDb.add(boletaCompra);
        }

        carritoPersonaRepository.deleteAll(carritoPersona);

        return boletaCompraRepository.saveAllAndFlush(boletaCompraDb);
    }

    private void enviarCorreo(String content, String correo) {
        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage mailMessage = javaMailSender.createMimeMessage();

                String htmlBody = readHtmlTemplate.readHtmlTemplate(content, "plantilla.html");
                mailMessage.setContent(htmlBody, "text/html; charset=UTF-8");
                mailMessage.setFrom(Objects.requireNonNull(environment.getProperty("spring.mail.username")));
                mailMessage.setSubject("Se ha recibido su pago correctamente");
                mailMessage.setRecipients(MimeMessage.RecipientType.TO, correo);
                javaMailSender.send(mailMessage);
            } catch (MessagingException e) {
                System.out.println(e.getMessage());
                throw new ServerErrorException();
            }
        });
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Bogota")
    public void revisarCompras() {
        List<CompraEntity> compras = (List<CompraEntity>) compraRepository.findAll();

        LocalDateTime fechaActual = LocalDateTime.now();

        EstadoCompraEntity estadoCompra = estadoCompraRepository
                .findByDescripcion("Cancelado")
                .orElseThrow(() -> new IdNotFoundException("estado_compra"));

        List<CompraEntity> comprasFiltered = compras
                .stream()
                .filter(compraEntity -> compraEntity.getEstadoCompra().getDescripcion().equals("En espera"))
                .toList();

        comprasFiltered.forEach(compra -> {
            if (compra.getFecha_creacion().isBefore(fechaActual.minusHours(24))) {
                compra.setEstadoCompra(estadoCompra);
                compraRepository.save(compra);
            }
        });
    }
}
