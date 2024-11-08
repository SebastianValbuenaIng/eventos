package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.controllers.administracion_empresas;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.administracion_empresas.GraduadoAdminEmpresasResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.administracion_empresas.IniciarSesionAdminEmpresasService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/iniciar-sesion-administracion-empresas")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class IniciarSesionControllerAdminEmpresas {
    private final IniciarSesionAdminEmpresasService iniciarSesionAdminEmpresasService;

    @GetMapping
    public ResponseEntity<GraduadoAdminEmpresasResponse> iniciarSesion(@RequestParam String documento) {
        return ResponseEntity.ok(iniciarSesionAdminEmpresasService.iniciarSesionAdminEmpresas(documento));
    }

    @GetMapping("/admin")
    public ResponseEntity<Map<String, String>> iniciarSesionAdmin(@RequestParam String correo) {
        return ResponseEntity.ok(iniciarSesionAdminEmpresasService.iniciarSesionAdmin(correo));
    }
}
