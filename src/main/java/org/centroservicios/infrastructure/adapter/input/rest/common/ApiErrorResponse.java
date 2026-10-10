package org.centroservicios.infrastructure.adapter.input.rest.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ErrorResponse", description = "Estructura estándar de errores de API")
public class ApiErrorResponse {

    @Schema(description = "Código único para rastrear el error en los logs", example = "d9b2d63d-a231-4ee6-8839-444738734538")
    private String errorId;

    @Schema(description = "Tipo de error devuelto", example = "VALIDATION_ERROR")
    private String typeError;

    @Schema(description = "Mensaje descriptivo del error", example = "El usuario con ID 5 no fue encontrado")
    private String message;

    @Schema(description = "Código HTTP del error", example = "401, 404 o 500")
    private int status;

    @Schema(description = "Fecha y hora del error", example = "2023-10-25T14:30:00")
    private LocalDateTime timestamp;

    private List<ErrorDetailDto> errorDetails;
}

