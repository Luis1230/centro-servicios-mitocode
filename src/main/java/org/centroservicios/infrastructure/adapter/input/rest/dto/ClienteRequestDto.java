package org.centroservicios.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ClienteRequestDto", description = "Información requerida para crear o actualizar un cliente")
public record ClienteRequestDto (

    @Schema(description = "Nombres del cliente", example = "Ana Maria", required = true)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    String nombres,

    @Schema(description = "Apellidos del cliente", example = "Torres Diaz", required = true)
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    String apellidos,

    @Schema(description = "Email del cliente", example = "luis_xx@gmail.com", required = true)
    @NotBlank(message = "El email es obligatorio")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    String email,

    @Schema(description = "Telefono del cliente", example = "985881122", required = true)
    @NotBlank(message = "El telefono es obligatorio")
    @Size(max = 30, message = "El telefono no puede superar los 30 caracteres")
    String telefono

) { }