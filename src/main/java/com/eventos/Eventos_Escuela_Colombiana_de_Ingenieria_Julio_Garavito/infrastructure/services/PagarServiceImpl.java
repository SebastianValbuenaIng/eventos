package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.services;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.CarritoPersonaEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.CompraEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.EstadoCompraEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.PersonaEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.CarritoPersonaRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.CompraRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.EstadoCompraRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.PersonaRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.PagarService;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.IdNotFoundException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.MessageBadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@Transactional
@AllArgsConstructor
public class PagarServiceImpl implements PagarService {
    private final PersonaRepository personaRepository;
    private final CarritoPersonaRepository carritoPersonaRepository;
    private final CompraRepository compraRepository;
    private final EstadoCompraRepository estadoCompraRepository;

    @Override
    public Map<String, Integer> pagar(String documento, Integer valor) {
        PersonaEntity personaFound = personaRepository
                .findByDocumento(documento)
                .orElseThrow(() -> new IdNotFoundException("persona"));

        List<CarritoPersonaEntity> carritoPersona = carritoPersonaRepository.findByPersona(personaFound);

        int valorTotal = getValorTotal(carritoPersona);

        System.out.println(valorTotal);

        if (valor != valorTotal) throw new MessageBadRequestException("El valor no es el adecuado");

        EstadoCompraEntity estadoCompra = estadoCompraRepository
                .findByDescripcion("En espera")
                .orElseThrow(() -> new IdNotFoundException("estado_compra"));

        CompraEntity compra = CompraEntity
                .builder()
                .fecha_creacion(LocalDateTime.now())
                .fecha_pago(null)
                .valor(valorTotal)
                .persona(personaFound)
                .estadoCompra(estadoCompra)
                .build();

        CompraEntity compraSaved = compraRepository.save(compra);

        return Map.of("id_referencia", compraSaved.getNro());
    }

    private static int getValorTotal(List<CarritoPersonaEntity> carritoPersona) {
        List<Integer> valoresFiltrados = carritoPersona.stream()
                .filter(carritoPersonaEntity -> carritoPersonaEntity.getBoleta().getRol().getDescripcion().equals("Invitado"))
                .map(carritoPersonaEntity -> carritoPersonaEntity.getBoleta().getValor())
                .toList();

        int valorConDescuentoParesInvitado = getValorConDescuento(valoresFiltrados);

        int valorBoleta = carritoPersona.stream()
                .filter(carritoPersonaEntity -> !carritoPersonaEntity.getBoleta().getRol().getDescripcion().equals("Invitado"))
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
    public void generatePay(String descripcion, String valor, String estado_pol) {
        System.out.println("descripcion " + descripcion);
        System.out.println("valor " + valor);
        System.out.println("estado_pol " + estado_pol);

        if (!Objects.equals(estado_pol, "4")) return;

    }
}
