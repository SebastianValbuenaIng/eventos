package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.controllers;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.InicioSesionResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.IniciarSesionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iniciar-sesion")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class IniciarSesionController {
    private final IniciarSesionService iniciarSesionService;

    // TODO: Cantidad de boletas en el carrito, si ya pagaron boletas
    @GetMapping
    public ResponseEntity<InicioSesionResponse> iniciarSesionAdministrativoEstudiante(@RequestParam String correo) {
        return ResponseEntity.ok(iniciarSesionService.iniciarSesion(correo));
    }

    // TODO: Cantidad de boletas en el carrito, si ya pagaron boletas
    @GetMapping("/graduado")
    public ResponseEntity<InicioSesionResponse> iniciarSesionGraduado(@RequestParam String documento) {
        return ResponseEntity.ok(iniciarSesionService.iniciarSesionGraduado(documento));
    }
}