package org.centroservicios.infrastructure.adapter.input.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.centroservicios.domain.services.ClienteService;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ClienteRequestDto;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

/**
 * Controller REST encargado de exponer las operaciones sobre clientes.
 *
 * <p>Delega toda la logica de negocio en {@link ClienteService}; este
 * controller solo se encarga de mapear rutas HTTP, validar la entrada
 * y traducir el {@code statusCode} del {@code ApiResponse} al codigo
 * HTTP real de la respuesta.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/mitocode/api/v1/centro-servicios/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    @POST
    @Path("/create")
    public Response createCliente(@Valid ClienteRequestDto request) {
        return Response.status(201)
                .entity(clienteService.createCliente(request))
                .build();
    }

    @GET
    @Path("/{busqueda}")
    public Response getClientesNombresApellidos(@PathParam("busqueda") String busqueda) {
        return Response.status(200)
                .entity(clienteService.buscarClientePorNombresCompletos(busqueda))
                .build();
    }

    @GET
    public Response getNombresActivos(
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
        return Response.status(200)
                .entity(clienteService.listarActivos(page, limit))
                .build();
    }
}
