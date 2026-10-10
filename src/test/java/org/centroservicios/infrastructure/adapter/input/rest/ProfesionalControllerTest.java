package org.centroservicios.infrastructure.adapter.input.rest;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import org.centroservicios.domain.services.ProfesionalService;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ProfesionalRequestDto;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ProfesionalResponseDto;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(ProfesionalController.class)
public class ProfesionalControllerTest {

    @InjectMock
    ProfesionalService profesionalService;

    @Test
    void postProfesionalController_return_created() {
        UUID generatedCode = UUID.randomUUID();
        ProfesionalResponseDto responseDto = new ProfesionalResponseDto(
                generatedCode,
                "Luis Aljandro",
                "Muñante Escate",
                "Ing. de sistemas",
                true
        );

        ApiResponse<ProfesionalResponseDto> apiResponse = ApiResponse.<ProfesionalResponseDto>builder()
                .data(responseDto)
                .statusCode(HttpResponseStatus.CREATED.code())
                .message("Profesional creado exitosamente")
                .timestamp(Instant.now())
                .build();

        when(profesionalService.createProfesional(any(ProfesionalRequestDto.class)))
                .thenReturn(Uni.createFrom().item(apiResponse));

        ProfesionalRequestDto requestDto = new ProfesionalRequestDto(
                "Luis Aljandro",
                "Muñante Escate",
                "Ing. de sistemas");

        given()
                .contentType(ContentType.JSON)
                .body(requestDto)
        .when()
                .post("/create")
        .then()
                .statusCode(201)
                .body("data.id",equalTo(generatedCode.toString()))
                .body("data.nombres",equalTo("Luis Aljandro"))
                .body("data.apellidos",equalTo("Muñante Escate"))
                .body("data.especialidad",equalTo("Ing. de sistemas"))
                .body("data.activo",equalTo(true))
        ;

        verify(profesionalService).createProfesional(requestDto);
    }
}
