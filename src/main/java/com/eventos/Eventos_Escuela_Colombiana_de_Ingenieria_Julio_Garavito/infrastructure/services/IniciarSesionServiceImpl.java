package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.services;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.models.response.InicioSesionResponse;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.CarritoPersonaEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.CompraEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.PersonaEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities.RolEntity;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.CarritoPersonaRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.CompraRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.PersonaRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories.RolRepository;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.infrastructure.abstract_services.IniciarSesionService;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.IdNotFoundException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.ServerErrorException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.helpers.AppMovilConnection;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.helpers.RegistroConnection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class IniciarSesionServiceImpl implements IniciarSesionService {
    private final AppMovilConnection appMovilConnection;
    private final RegistroConnection registroConnection;
    private final PersonaRepository personaRepository;
    private final CarritoPersonaRepository carritoPersonaRepository;
    private final CompraRepository compraRepository;
    private final RolRepository rolRepository;

    @Override
    public InicioSesionResponse iniciarSesion(String correo) {
        try {
            String consulta = String.format("SELECT * FROM dbo.personas_activas WHERE email = '%s'", correo);

            Map<String, Object> returnQuery = appMovilConnection.executeSelectSql(consulta);

            if (returnQuery.isEmpty()) throw new IdNotFoundException("app_movil");

            Optional<PersonaEntity> foundPersona = personaRepository
                    .findByDocumento(String.valueOf(returnQuery.get("nro_documento")));

            if (foundPersona.isEmpty()) {
                String rolTipo = String.valueOf(returnQuery.get("tipo_rol")).trim().equals("Estudiantes") ? "Estudiante"
                        : String.valueOf(returnQuery.get("tipo_rol")).trim().equals("Administrativo") ? "Administrativo"
                        : "Profesor";

                RolEntity rol = rolRepository
                        .findByDescripcion(rolTipo)
                        .orElseThrow(() -> new IdNotFoundException("rol"));

                PersonaEntity newPersona = PersonaEntity
                        .builder()
                        .correo(String.valueOf(returnQuery.get("email")))
                        .rol(rol)
                        .documento(String.valueOf(returnQuery.get("nro_documento")))
                        .telefono(String.valueOf(returnQuery.get("telefono")))
                        .nombre(String.valueOf(returnQuery.get("nombre")))
                        .build();

                personaRepository.save(newPersona);

                return InicioSesionResponse
                        .builder()
                        .area(String.valueOf(returnQuery.get("area")).trim())
                        .correo(String.valueOf(returnQuery.get("email")))
                        .rol(String.valueOf(returnQuery.get("tipo_rol")))
                        .direccion(String.valueOf(returnQuery.get("direccion")))
                        .nro_documento(String.valueOf(returnQuery.get("nro_documento")))
                        .telefono(String.valueOf(returnQuery.get("telefono")))
                        .nombre(String.valueOf(returnQuery.get("nombre")))
                        .carrito_boleta(false)
                        .cantidad_boletas_carrito(0)
                        .pago_boleta_rol_principal(false)
                        .build();
            }

            List<CarritoPersonaEntity> listCarritoPersona = carritoPersonaRepository.findByPersona(foundPersona.get());

            boolean carrito_persona = !listCarritoPersona.isEmpty();

            // Si ya pago su boleta principal o si ya la tiene en el carrito
            boolean pago_boleta_principal = false;

            List<CompraEntity> listCompraPersona = compraRepository.findAllByPersona(foundPersona.get());

            if (!listCompraPersona.isEmpty()) pago_boleta_principal = true;
            else {
                List<CarritoPersonaEntity> findBoletaPrincipalCarrito = listCarritoPersona
                        .stream()
                        .filter(carritoPersonaEntity -> !carritoPersonaEntity.getBoleta().getRol().getDescripcion().equals("Invitado"))
                        .toList();

                if (!findBoletaPrincipalCarrito.isEmpty()) pago_boleta_principal = true;
            }

            return InicioSesionResponse
                    .builder()
                    .area(String.valueOf(returnQuery.get("area")).trim())
                    .correo(String.valueOf(returnQuery.get("email")))
                    .rol(String.valueOf(returnQuery.get("tipo_rol")))
                    .direccion(String.valueOf(returnQuery.get("direccion")))
                    .nro_documento(String.valueOf(returnQuery.get("nro_documento")))
                    .telefono(String.valueOf(returnQuery.get("telefono")))
                    .nombre(String.valueOf(returnQuery.get("nombre")))
                    .carrito_boleta(carrito_persona)
                    .cantidad_boletas_carrito(listCarritoPersona.size())
                    .pago_boleta_rol_principal(pago_boleta_principal)
                    .build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new ServerErrorException();
        }
    }

    @Override
    public InicioSesionResponse iniciarSesionGraduado(String nro_documento) {
        try {
            String consulta = String.format("""
                select distinct b.national_id, b.nombre, a.documento, a.email, a.tel_residencia, a.cel
                from uge.migracion_directa_graduados b
                inner join uge.datos_egre a on (b.NATIONAL_ID = a.documento) where documento = '%s'""", nro_documento);

            Map<String, Object> returnQuery = registroConnection.executeSelectSql(consulta);

            if (returnQuery.isEmpty()) throw new IdNotFoundException("registro_graduado");

            Optional<PersonaEntity> foundPersona = personaRepository.findByDocumento(String.valueOf(returnQuery.get("documento")));

            if (foundPersona.isEmpty()) {
                RolEntity rol = rolRepository
                        .findByDescripcion("Graduado")
                        .orElseThrow(() -> new IdNotFoundException("rol"));

                PersonaEntity newPersona = PersonaEntity
                        .builder()
                        .correo(String.valueOf(returnQuery.get("email")))
                        .rol(rol)
                        .documento(String.valueOf(returnQuery.get("documento")))
                        .telefono(String.valueOf(returnQuery.get("tel_residencia")))
                        .nombre(String.valueOf(returnQuery.get("nombre")))
                        .build();

                personaRepository.save(newPersona);

                return InicioSesionResponse
                        .builder()
                        .area(null)
                        .correo(String.valueOf(returnQuery.get("email")))
                        .rol("Graduado")
                        .direccion(null)
                        .nro_documento(String.valueOf(returnQuery.get("documento")))
                        .telefono(String.valueOf(returnQuery.get("tel_residencia")))
                        .nombre(String.valueOf(returnQuery.get("nombre")))
                        .carrito_boleta(false)
                        .cantidad_boletas_carrito(0)
                        .pago_boleta_rol_principal(false)
                        .build();
            }

            List<CarritoPersonaEntity> listCarritoPersona = carritoPersonaRepository.findByPersona(foundPersona.get());

            boolean carrito_persona = !listCarritoPersona.isEmpty();

            // Si ya pago su boleta principal o si ya la tiene en el carrito
            boolean pago_boleta_principal = false;

            List<CompraEntity> listCompraPersona = compraRepository.findAllByPersona(foundPersona.get());

            if (!listCompraPersona.isEmpty()) pago_boleta_principal = true;
            else {
                List<CarritoPersonaEntity> findBoletaPrincipalCarrito = listCarritoPersona
                        .stream()
                        .filter(carritoPersonaEntity -> !carritoPersonaEntity.getBoleta().getRol().getDescripcion().equals("Invitado"))
                        .toList();

                if (!findBoletaPrincipalCarrito.isEmpty()) pago_boleta_principal = true;
            }

            return InicioSesionResponse
                    .builder()
                    .area(null)
                    .correo(String.valueOf(returnQuery.get("email")))
                    .rol("Graduado")
                    .direccion(null)
                    .nro_documento(String.valueOf(returnQuery.get("documento")))
                    .telefono(String.valueOf(returnQuery.get("tel_residencia")))
                    .nombre(String.valueOf(returnQuery.get("nombre")))
                    .carrito_boleta(carrito_persona)
                    .cantidad_boletas_carrito(listCarritoPersona.size())
                    .pago_boleta_rol_principal(pago_boleta_principal)
                    .build();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new ServerErrorException();
        }
    }
}
