package org.centroservicios.infrastructure.adapter.input.rest;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.centroservicios.domain.services.ReservaService;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiErrorResponse;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.dto.HorarioDisponibleRequestDto;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ReservaRequestDto;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ReservaUpdateRequestDto;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/mitocode/api/v1/centro-servicios/reserva")
public class ReservaController {

    private final ReservaService reservaService;

    @POST
    @Path("/create")
    public Uni<Response> createReserva(@Valid ReservaRequestDto request) {

        return reservaService.createReserva(request)
                .map(apiResponse -> Response.status(Response.Status.OK)
                        .entity(apiResponse)
                        .build());

    }

    @PUT
    @Path("/modificar-reserva/{id}")
    public Uni<Response> updateReserva(@PathParam("id") UUID id, @Valid ReservaUpdateRequestDto request) {

        return reservaService.updateReserva(id, request)
                .map(apiResponse -> Response.status(Response.Status.OK)
                        .entity(apiResponse)
                        .build());

    }

    @PUT
    @Path("/cancelar-reserva/{id}")
    public Uni<Response> cancelarReserva(@PathParam("id") UUID id) {

        return reservaService.cancelarReserva(id)
                .map(apiResponse -> Response.status(Response.Status.OK)
                        .entity(apiResponse)
                        .build());

    }

    @GET
    @Path("/profesionales-por-reservas")
    @Operation(summary = "Profesionales ordenados por número de reservas activas (descendente)")
    public Uni<Response> profesionalesPorReservas() {
        return reservaService.profesionalesPorReservasActivas()
                .map(apiResponse -> Response.status(Response.Status.OK)
                        .entity(apiResponse)
                        .build());
    }

    @GET
    @Path("/reservas-por-fecha")
    @Operation(summary = "Reservas activas agrupadas por fecha")
    public Uni<Response> reservasPorFecha() {
        return reservaService.reservasPorFecha()
                .map(apiResponse -> Response.status(Response.Status.OK)
                        .entity(apiResponse)
                        .build());
    }
}
