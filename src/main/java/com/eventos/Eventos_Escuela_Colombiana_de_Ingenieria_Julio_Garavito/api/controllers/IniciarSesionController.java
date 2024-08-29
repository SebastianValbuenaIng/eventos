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

    @GetMapping
    public ResponseEntity<Object> iniciarSesionAdministrativoEstudiante(@RequestParam String correo) {
        return ResponseEntity.ok(iniciarSesionService.iniciarSesion(correo));
    }

    @GetMapping("/graduado")
    public ResponseEntity<InicioSesionResponse> iniciarSesionGraduado(@RequestParam String documento) {
        return ResponseEntity.ok(iniciarSesionService.iniciarSesionGraduado(documento));
    }

    @PostMapping("/graduado/datos")
    public ResponseEntity<Object> insertDatosGraduado(
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String telefono,
            @RequestParam String documento
    ) {
        return ResponseEntity.ok(iniciarSesionService.insertDatosGraduado(correo, telefono, documento));
    }
}
