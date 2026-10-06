package org.centroservicios.infrastructure.adapter.input.rest;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.centroservicios.domain.services.ProfesionalService;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ProfesionalRequestDto;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

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
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/mitocode/api/v1/centro-servicios/profesional")
public class ProfesionalController {

    private final ProfesionalService profesionalService;

    @POST
    @Path("/create")
    public Uni<Response> createProfesional(@Valid ProfesionalRequestDto request) {

        return profesionalService.createProfesional(request)
                .map(apiResponse -> Response.status(Response.Status.CREATED)
                        .entity(apiResponse)
                        .build());
    }

    @GET
    @Path("/{busqueda}")
    public Uni<Response> getProfesionalesNombresApellidos(@PathParam("busqueda") String busqueda) {

        return profesionalService.buscarProfesionalPorNombresCompletos(busqueda)
                .map(apiResponse -> Response.status(Response.Status.OK)
                        .entity(apiResponse)
                        .build());
    }

    @GET
    public Uni<Response> getProfesionalesActivos(
            @Parameter(
                    description = "Número de página a consultar",
                    example = "1",
                    schema = @Schema(defaultValue = "1")
            )
            @QueryParam("page") @DefaultValue("1")
            @Min(value = 1, message = "La página mínima es 1")
            int page,

            @Parameter(
                    description = "Cantidad de registros por página",
                    example = "10",
                    schema = @Schema(defaultValue = "10", maximum = "100")
            )
            @QueryParam("limit") @DefaultValue("10")
            @Min(10)
            @Max(value = 20, message = "No puedes pedir más de 20 registros")
            int limit
    ) {

        return profesionalService.listarActivos(page, limit)
                .map(apiResponse -> Response.status(Response.Status.OK)
                        .entity(apiResponse)
                        .build());
    }

}
