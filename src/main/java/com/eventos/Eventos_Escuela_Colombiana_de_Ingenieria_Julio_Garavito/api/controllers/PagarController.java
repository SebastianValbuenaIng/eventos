package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.controllers;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.PagarService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pagar")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class PagarController {
    private final PagarService pagarService;

    @PostMapping(path = "/receivePay", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> notify(String descripcion, String valor, String estado_pol) {
        pagarService.generatePay(descripcion, valor, estado_pol);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Map<String, Long>> pagar(@RequestParam String documento, @RequestParam Integer valor) {
        return ResponseEntity.ok(pagarService.pagar(documento, valor));
    }
}
