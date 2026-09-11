package org.centroservicios.infrastructure.adapter.input.rest;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.centroservicios.domain.services.ProfesionalService;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ProfesionalRequestDto;

/**
 * Controller REST encargado de exponer las operaciones sobre profesionales.
 *
 * <p>Delega toda la logica de negocio en {@link ProfesionalService}; este
 * controller solo se encarga de mapear rutas HTTP, validar la entrada
 * y traducir el {@code statusCode} del {@code ApiResponse} al codigo
 * HTTP real de la respuesta.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Path("/mitocode/api/v1/centro-servicios")
public class ProfesionalController {

    private final ProfesionalService profesionalService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response createProfesional(@Valid ProfesionalRequestDto request) {
        return Response
                .status(201)
                .entity(profesionalService.createProfesional(request))
                .build();
    }


}
