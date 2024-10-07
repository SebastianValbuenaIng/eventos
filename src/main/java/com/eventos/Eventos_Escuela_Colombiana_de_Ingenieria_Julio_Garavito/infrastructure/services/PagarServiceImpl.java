package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.services;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.*;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.*;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.PagarService;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.IdNotFoundException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.ServerErrorException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.helpers.ReadHTMLTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
@AllArgsConstructor
public class PagarServiceImpl implements PagarService {
    private final PersonaRepository personaRepository;
    private final CarritoPersonaRepository carritoPersonaRepository;
    private final CompraRepository compraRepository;
    private final EstadoCompraRepository estadoCompraRepository;
    private final BoletaCompraRepository boletaCompraRepository;
    private final HistorialPagoCompraMenorRepository historialPagoCompraMenorRepository;
    private final JavaMailSender javaMailSender;
    private final Environment environment;
    private final ReadHTMLTemplate readHtmlTemplate;

    @Override
    public void generatePay(String descripcion, String value, String estado_pol) {
        if (!Objects.equals(estado_pol, "4")) return;

        EstadoCompraEntity estadoCompraEnEspera = estadoCompraRepository
                .findByDescripcion("En espera")
                .orElseThrow(() -> new IdNotFoundException("estado_compra"));

        CompraEntity compraFound = compraRepository
                .findByNumeroReferenciaAndEstadoCompra(Long.valueOf(descripcion.split("#")[1]), estadoCompraEnEspera)
                .orElseThrow(() -> new IdNotFoundException("compra"));

        PersonaEntity personaFound = personaRepository
                .findByDocumento(compraFound.getPersona().getDocumento())
                .orElseThrow(() -> new IdNotFoundException("persona"));

        int valorRecibido = Integer.parseInt(value.split("\\.")[0]);

        boolean validPay = (valorRecibido >= compraFound.getValor());

        if (!validPay) {
            HistorialPagoCompraMenorEntity historialPagoCompraMenorEntity = HistorialPagoCompraMenorEntity
                    .builder()
                    .fecha(LocalDateTime.now())
                    .descripcion_payu(descripcion)
                    .valor_recibido(valorRecibido)
                    .build();

            historialPagoCompraMenorRepository.saveAndFlush(historialPagoCompraMenorEntity);

            return;
        }

        // * Limpiar del carrito de la persona e insertar en boleta_compra
        List<BoletaCompraEntity> boletasCompras = insertsBoletaCompraAndDeleteCarritoPersona(personaFound, compraFound);

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

    public void enviarCorreos(String correo) {
        PersonaEntity personaFound = personaRepository
                .findByCorreo(correo)
                .orElseThrow(() -> new IdNotFoundException("persona"));

        List<CompraEntity> comprasPersona = compraRepository
                .findAllByPersona(personaFound);

        List<CompraEntity> compraFound = comprasPersona
                .stream()
                .filter(compra -> compra.getEstadoCompra().getDescripcion().equals("Completado"))
                .toList();

        compraFound.forEach(compra -> {
            List<BoletaCompraEntity> boletasCompras = boletaCompraRepository.findAllByCompra(compra);

            String htmlBody = getEmail(boletasCompras, personaFound, compra);

            // * Enviar correo
            enviarCorreo(htmlBody, correo);
        });
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
                <p class="texto">Se le informa que se ha recibido su pago correctamente por un valor de: <b>%s COP</b>. Recuerde que el día del evento, para <b>ingresar al restaurante</b>, debe presentar su boleta con la respectiva numeración.</p>
                <p>El número de su(s) boletas es: </p>
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

//    @Scheduled(cron = "0 0 0 * * *", zone = "America/Bogota")
//    public void revisarCompras() {
//        List<CompraEntity> compras = (List<CompraEntity>) compraRepository.findAll();
//
//        LocalDateTime fechaActual = LocalDateTime.now();
//
//        EstadoCompraEntity estadoCompra = estadoCompraRepository
//                .findByDescripcion("Cancelado")
//                .orElseThrow(() -> new IdNotFoundException("estado_compra"));
//
//        List<CompraEntity> comprasFiltered = compras
//                .stream()
//                .filter(compraEntity -> compraEntity.getEstadoCompra().getDescripcion().equals("En espera"))
//                .toList();
//
//        comprasFiltered.forEach(compra -> {
//            if (compra.getFecha_creacion().isBefore(fechaActual.minusHours(24))) {
//                compra.setEstadoCompra(estadoCompra);
//                compraRepository.save(compra);
//            }
//        });
//    }
}
