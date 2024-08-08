package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.controllers;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.request.BoletaPersonaRequest;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.BoletaCarritoResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.BoletaRolResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.BoletaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/boleta")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class BoletaController {
    private final BoletaService boletaService;

    @GetMapping
    public ResponseEntity<BoletaRolResponse> getBoletasByRol(@RequestParam String rol) {
        return ResponseEntity.ok(boletaService.getBoletasByRol(rol));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> agregarAlCarrito(@RequestBody BoletaPersonaRequest boletaPersonaRequest) {
        return ResponseEntity.ok(boletaService.agregarAlCarrito(boletaPersonaRequest));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> eliminarDelCarrito(@RequestParam Integer id_carrito_persona) {
        return ResponseEntity.ok(boletaService.eliminarDelCarrito(id_carrito_persona));
    }

    @GetMapping("/carrito")
    public ResponseEntity<List<BoletaCarritoResponse>> getBoletasCarrito(@RequestParam String nro_documento) {
        return ResponseEntity.ok(boletaService.getBoletasCarritoPersona(nro_documento));
    }

    @GetMapping("/pagadas")
    public ResponseEntity<List<Object>> getBoletasPagadas() {
        return ResponseEntity.ok(List.of());
    }
}
