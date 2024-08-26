package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.controllers;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.AdminBoletaPagadaResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.AdminCompraResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.CompraAdminResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.AdministradorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    private final AdministradorService administradorService;

    @GetMapping("/boletasPersonas")
    public ResponseEntity<List<AdminBoletaPagadaResponse>> boletasPagadas(@RequestParam String correo) {
        return ResponseEntity.ok(administradorService.getBoletasAdmin(correo));
    }

    @GetMapping("/comprasPersonas")
    public ResponseEntity<List<AdminCompraResponse>> getComprasPersonas(@RequestParam String correo) {
        return ResponseEntity.ok(administradorService.getCompras(correo));
    }

    @GetMapping("/comprasPersonas/{id}")
    public ResponseEntity<CompraAdminResponse> getComprasPersonas(@RequestParam String correo, @PathVariable Integer id) {
        return ResponseEntity.ok(administradorService.getCompraPersona(correo, id));
    }
}
