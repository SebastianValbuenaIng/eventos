package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.controllers.administracion_empresas;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.request.administracion_empresas.ParticipanteRequest;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.administracion_empresas.ParticipanteAdminEmpresasService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/participante-administracion-empresas")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ParticipanteController {
    private final ParticipanteAdminEmpresasService participanteAdminEmpresasService;

    @PostMapping
    public ResponseEntity<Map<String, String>> inscripcionParticipante (@RequestBody ParticipanteRequest participanteRequest) {
        return ResponseEntity.ok(participanteAdminEmpresasService.inscripcionPersona(participanteRequest));
    }

    @GetMapping("/reporte")
    public ResponseEntity<byte[]> downloadReport(@RequestParam String correo) {
        byte[] report = participanteAdminEmpresasService.generateReport(correo);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reporte_participantes.xlsx");

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }
}
