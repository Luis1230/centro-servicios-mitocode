package org.centroservicios.infrastructure.adapter.input.rest;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.centroservicios.domain.services.HorarioDisponibleService;
import org.centroservicios.infrastructure.adapter.input.rest.dto.HorarioDisponibleRequestDto;

@Slf4j
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/mitocode/api/v1/centro-servicios/horario-disponible")
public class HorarioDisponibleController {

    private final HorarioDisponibleService horarioDisponibleService;

    @POST
    @Path("/create")
    public Uni<Response> createHorarioDisponible(@Valid HorarioDisponibleRequestDto request) {

        return horarioDisponibleService.createHorarioDisponible(request)
                .map(apiResponse -> Response.status(Response.Status.OK)
                .entity(apiResponse)
                .build());

    }
}
