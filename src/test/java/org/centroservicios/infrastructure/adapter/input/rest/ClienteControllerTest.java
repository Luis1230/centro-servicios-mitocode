package org.centroservicios.infrastructure.adapter.input.rest;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.centroservicios.domain.services.ClienteService;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ClienteResponseDto;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(ClienteController.class)
public class ClienteControllerTest {

    @InjectMock
    ClienteService clienteService;

    @Test
    void getClienteController_returns_Ok() {
        UUID generatedCode = UUID.randomUUID();
        ClienteResponseDto responseDto = new ClienteResponseDto(
                generatedCode,
                "Luis Aljandro",
                "Muñante Escate",
                "luis_ccc@gmail.com",
                "985885478",
                true
        );

        ApiResponse<List<ClienteResponseDto>> apiResponse = ApiResponse.<List<ClienteResponseDto>>builder()
                .data(List.of(responseDto))
                .statusCode(HttpResponseStatus.OK.code())
                .message("Se obtuvo correctamente la información requerida.")
                .totalElements(1)
                .timestamp(Instant.now())
                .build();

        when(clienteService.buscarClientePorNombresCompletos(any()))
                .thenReturn(Uni.createFrom().item(apiResponse));

        given()
                .pathParams("busqueda", "Luis")
        .when()
                .get("/{busqueda}")
        .then()
                .statusCode(200)
                .body("data", hasSize(1))
                .body("data[0].id", equalTo(generatedCode.toString()))
                .body("data[0].nombres",equalTo("Luis Aljandro"))
                .body("data[0].apellidos",equalTo("Muñante Escate"))
                .body("data[0].email", equalTo("luis_ccc@gmail.com"))
                .body("data[0].telefono", equalTo("985885478"))
                .body("data[0].activo",equalTo(true))
                .body("totalElements", equalTo(1));

        verify(clienteService).buscarClientePorNombresCompletos("Luis");



    }

}
